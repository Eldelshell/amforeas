/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
 *
 * This file is part of Jongo.
 * Jongo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * Jongo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jongo.  If not, see <http://www.gnu.org/licenses/>.
 */

package jongo.jdbc;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jongo.JongoUtils;
import jongo.config.DatabaseConfiguration;
import jongo.config.JongoConfiguration;
import jongo.handler.JongoResultSetHandler;
import jongo.handler.ResultSetMetaDataHandler;
import jongo.rest.xstream.Row;
import jongo.sql.*;
import jongo.sql.dialect.Dialect;
import jongo.sql.dialect.DialectFactory;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class in charge of executing SQL statements against a given RDBMS.
 * @author Alejandro Ayuso
 */
public class JDBCExecutor {

    private static final Logger l = LoggerFactory.getLogger(JDBCExecutor.class);
    private static final JongoConfiguration conf = JongoConfiguration.instanceOf();
    
    /**
     * Executes the given {@link jongo.sql.Delete} object
     * @param delete a {@link jongo.sql.Delete} instance
     * @return number of records deleted
     * @throws SQLException from the QueryRunner
     * @see org.apache.commons.dbutils.QueryRunner
     */
    public static int delete(final Delete delete) throws SQLException {
        l.debug(delete.toString());
        DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(delete.getTable().getDatabase());
        QueryRunner run = JDBCConnectionFactory.getQueryRunner(dbconf);
        Dialect dialect = DialectFactory.getDialect(dbconf);
        
        try {
            int deleted = run.update(dialect.toStatementString(delete), JongoUtils.parseValue(delete.getId()));
            l.debug("Deleted " + deleted + " records.");
            return deleted;
        } catch (SQLException ex) {
            l.debug(ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Executes the given {@link jongo.sql.Insert} object
     * @param insert a {@link jongo.sql.Insert} instance
     * @return number of records inserted
     * @throws SQLException from the QueryRunner
     * @see org.apache.commons.dbutils.QueryRunner
     */
    public static int insert(final Insert insert) throws SQLException {
        l.debug(insert.toString());
        
        DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(insert.getTable().getDatabase());
        QueryRunner run = JDBCConnectionFactory.getQueryRunner(dbconf);
        Dialect dialect = DialectFactory.getDialect(dbconf);
        
        try {
            int inserted;
            if(insert.getColumns().isEmpty())
                inserted = run.update(dialect.toStatementString(insert));
            else
                inserted = run.update(dialect.toStatementString(insert), JongoUtils.parseValues(insert.getValues()));
            
            l.debug("Inserted " + inserted + " records.");
            return inserted;
        } catch (SQLException ex) {
            l.debug(ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Executes the given {@link jongo.sql.Update} object
     * @param update a {@link jongo.sql.Update} instance
     * @return a List of {@link jongo.rest.xstream.Row} with the modified records
     * @throws SQLException from the QueryRunner
     * @see org.apache.commons.dbutils.QueryRunner
     */
    public static List<Row> update(final Update update) throws SQLException {
        l.debug(update.toString());
        
        DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(update.getTable().getDatabase());
        QueryRunner run = JDBCConnectionFactory.getQueryRunner(dbconf);
        Dialect dialect = DialectFactory.getDialect(dbconf);
        
        List<Row> results = new ArrayList<Row>();
        try {
            int ret = run.update(dialect.toStatementString(update), JongoUtils.parseValues(update.getParameters()));
            if(ret != 0){
                results = get(update.getSelect(), false);
            }
        } catch (SQLException ex) {
            l.error(ex.getMessage());
            throw ex;
        }
        l.debug("Updated " + results.size() + " records.");
        return results;
    }
    
    /**
     * Executes the given {@link jongo.sql.Select} object and returns all or one record depending on the value
     * of the allRecords variable
     * @param select a {@link jongo.sql.Select} instance
     * @param allRecords return all (true) records or one (false) record.
     * @return a List of {@link jongo.rest.xstream.Row} with the records found by the statement.
     * @throws SQLException from the QueryRunner
     * @see org.apache.commons.dbutils.QueryRunner
     * @see jongo.handler.JongoResultSetHandler
     */
    public static List<Row> get(final Select select, final boolean allRecords) throws SQLException {
        l.debug(select.toString());
        List<Row> response = null;
        
        DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(select.getTable().getDatabase());
        QueryRunner run = JDBCConnectionFactory.getQueryRunner(dbconf);
        Dialect dialect = DialectFactory.getDialect(dbconf);
        
        ResultSetHandler<List<Row>> res = new JongoResultSetHandler(allRecords);
        
        if(select.isAllRecords()){
            try {
                response = run.query(dialect.toStatementString(select), res);
            } catch (SQLException ex) {
                l.debug(ex.getMessage());
                throw ex;
            }
        }else{
            try {
                response = run.query(dialect.toStatementString(select), res, JongoUtils.parseValue(select.getParameter().getValue()));
            } catch (SQLException ex) {
                l.debug(ex.getMessage());
                throw ex;
            }
        }
        
        return response;
    }
    
    /**
     * Executes the given {@link org.jongo.jdbc.DynamicFinder} object.
     * @param database database name or schema where to execute the {@link org.jongo.jdbc.DynamicFinder}
     * @param df an instance of {@link org.jongo.jdbc.DynamicFinder}
     * @param limit an instance of {@link jongo.jdbc.LimitParam}
     * @param order an instance of {@link jongo.jdbc.OrderParam}
     * @param params a vararg of Object instances used as parameters for the QueryRunner.
     * @return a List of {@link jongo.rest.xstream.Row} with the records found by the DynamicFinder.
     * @throws SQLException from the QueryRunner
     * @see org.apache.commons.dbutils.QueryRunner
     * @see jongo.sql.dialect.Dialect
     */
    public static List<Row> find(final String database, final DynamicFinder df, final LimitParam limit, final OrderParam order, Object... params) throws SQLException{
        l.debug(df.getSql());
        l.debug(JongoUtils.varargToString(params));
        
        DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(database);
        Dialect dialect = DialectFactory.getDialect(dbconf);
        String query = dialect.toStatementString(df, limit, order);
        
        QueryRunner run = JDBCConnectionFactory.getQueryRunner(dbconf);
        ResultSetHandler<List<Row>> res = new JongoResultSetHandler(true);
        try {
            List<Row> results = run.query(query, res, params);
            l.debug("Received " + results.size() + " results.");
            return results;
        } catch (SQLException ex) {
            l.debug(ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Executes a given {@link jongo.sql.Select} object and returns the metadata associated to the results.
     * @param select a {@link jongo.sql.Select} instance which should only retrieve one result.
     * @return a List of {@link jongo.rest.xstream.Row} with the metadata obtained 
     * with the {@link jongo.sql.Select} statement
     * @throws SQLException SQLException from the QueryRunner
     * @see org.apache.commons.dbutils.QueryRunner
     * @see jongo.handler.ResultSetMetaDataHandler
     */
    public static List<Row> getTableMetaData(final Select select) throws SQLException {
        l.debug("Obtaining metadata from table " + select.toString());
        
        ResultSetHandler<List<Row>> res = new ResultSetMetaDataHandler();
        
        DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(select.getTable().getDatabase());
        QueryRunner run = JDBCConnectionFactory.getQueryRunner(dbconf);
        Dialect dialect = DialectFactory.getDialect(dbconf);
        
        try {
            List<Row> results = run.query(dialect.toStatementString(select), res);
            l.debug("Received " + results.size() + " results.");
            return results;
        } catch (SQLException ex) {
            l.debug(ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Executes the given stored procedure or function in the RDBMS using the given List 
     * of {@link jongo.jdbc.StoredProcedureParam}.
     * @param database database name or schema where to execute the stored procedure or function
     * @param queryName the name of the stored procedure or function. This gets converted to a {call foo()} statement.
     * @param params a List of {@link jongo.jdbc.StoredProcedureParam} used by the stored procedure or function.
     * @return a List of {@link jongo.rest.xstream.Row} with the results of the stored procedure (if out parameters are given)
     * or the results of the function.
     * @throws SQLException
     */
    public static List<Row> executeQuery(final String database, final String queryName, final List<StoredProcedureParam> params) throws SQLException{
        l.debug("Executing stored procedure " + database + "." + queryName);
        
        DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(database);
        QueryRunner run = JDBCConnectionFactory.getQueryRunner(dbconf);
        final String call = JongoUtils.getCallableStatementCallString(queryName, params.size());
        List<Row> rows = new ArrayList<Row>();
        
        Connection conn = null;
        CallableStatement cs = null;
        try{
            l.debug("Obtain connection from datasource");
            conn = run.getDataSource().getConnection();
            
            l.debug("Create callable statement for " + call);
            cs = conn.prepareCall(call);
            
            l.debug("Add parameters to callable statement");
            final List<StoredProcedureParam> outParams = addParameters(cs, params);
            
            l.debug("Execute callable statement");
            if(cs.execute()){
                l.debug("Got a result set " + queryName);
                ResultSet rs = cs.getResultSet();
                JongoResultSetHandler handler = new JongoResultSetHandler(true);
                rows = handler.handle(rs);
            }else if(!outParams.isEmpty()){
                l.debug("No result set, but we are expecting OUT values from " + queryName);
                Map<String, String> results = new HashMap<String, String>();
                for(StoredProcedureParam p : outParams){
                    results.put(p.getName(), cs.getString(p.getIndex())); // thank $deity we only return strings
                }
                rows.add(new Row(0, results));
            }
        }catch(SQLException ex){
            l.debug(ex.getMessage());
            throw ex;
        }finally{
            try{ if(cs != null   && !cs.isClosed())   cs.close();   } catch (SQLException ex){ l.debug(ex.getMessage()); }
            try{ if(conn != null && !conn.isClosed()) conn.close(); } catch (SQLException ex){ l.debug(ex.getMessage()); }
        }
        l.debug("Received " + rows.size() + " results.");
        return rows;
    }
    
    /**
     * Close all connections to the databases
     */
    public static void shutdown(){
        l.debug("Shutting down JDBC connections");
        try {
            JDBCConnectionFactory.closeConnections();
        } catch (Exception ex) {
            l.warn("Failed to close connection to database?");
            l.debug(ex.getMessage());
        }
    }
    
    /**
     * For a given database or schema, execute the statement returned by the {@link jongo.sql.dialect.Dialect} listOfTablesStatement()
     * method and return a List of {@link jongo.rest.xstream.Row} with all the tables available.
     * @param database the name of the database to query.
     * @return a List of {@link jongo.rest.xstream.Row} with all the tables available.
     * @throws SQLException 
     */
    public static List<Row> getListOfTables(final String database) throws SQLException{
        l.debug("Obtaining the list of tables for the database " + database);
//        if(!conf.allowListTables()){
//            throw JongoJDBCExceptionFactory.getException(database, "Cant read database metadata. Access Denied", JongoJDBCException.ILLEGAL_READ_CODE);
//        }
        ResultSetHandler<List<Row>> res = new JongoResultSetHandler(true);
        DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(database);
        Dialect dialect = DialectFactory.getDialect(dbconf);
        QueryRunner run = JDBCConnectionFactory.getQueryRunner(dbconf);
        
        try {
            List<Row> results = run.query(dialect.listOfTablesStatement(), res);
            l.debug("Received " + results.size() + " results.");
            return results;
        } catch (SQLException ex) {
            throw ex;
        }
    }
    
    /**
     * Utility method which registers in a CallableStatement object the different {@link jongo.jdbc.StoredProcedureParam}
     * instances in the given list. Returns a List of {@link jongo.jdbc.StoredProcedureParam} with all the OUT parameters
     * registered in the CallableStatement
     * @param cs the CallableStatement object where the parameters are registered.
     * @param params a list of {@link jongo.jdbc.StoredProcedureParam}
     * @return a list of OUT {@link jongo.jdbc.StoredProcedureParam} 
     * @throws SQLException if we fail to register any of the parameters in the CallableStatement
     */
    private static List<StoredProcedureParam> addParameters(final CallableStatement cs, final List<StoredProcedureParam> params) throws SQLException{
        List<StoredProcedureParam> outParams = new ArrayList<StoredProcedureParam>();
        int i = 1;
        for(StoredProcedureParam p : params){
            final Integer sqlType = p.getType();
            if(p.isOutParameter()){
                l.debug("Adding OUT parameter " + p.toString());
                cs.registerOutParameter(i++, sqlType);
                outParams.add(p);
            }else{
                l.debug("Adding IN parameter " + p.toString());
                switch(sqlType){
                    case Types.BIGINT:
                    case Types.INTEGER:
                    case Types.TINYINT:
//                    case Types.NUMERIC:
                        cs.setInt(i++, Integer.valueOf(p.getValue())); break;
                    case Types.DATE:
                        cs.setDate(i++, (Date)JongoUtils.parseValue(p.getValue())); break;
                    case Types.TIME:
                        cs.setTime(i++, (Time)JongoUtils.parseValue(p.getValue())); break;
                    case Types.TIMESTAMP:
                        cs.setTimestamp(i++, (Timestamp)JongoUtils.parseValue(p.getValue())); break;
                    case Types.DECIMAL:
                        cs.setBigDecimal(i++, (BigDecimal)JongoUtils.parseValue(p.getValue())); break;
                    case Types.DOUBLE:
                        cs.setDouble(i++, Double.valueOf(p.getValue())); break;
                    case Types.FLOAT:
                        cs.setLong(i++, Long.valueOf(p.getValue())); break;
                    default:
                        cs.setString(i++, p.getValue()); break;
                }
            }
        }
        return outParams;
    }
}
