/**
 * Copyright (C) Alejandro Ayuso
 *
 * This file is part of Amforeas. Amforeas is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Amforeas is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Amforeas. If not, see <http://www.gnu.org/licenses/>.
 */

package amforeas.jdbc;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import amforeas.AmforeasUtils;
import amforeas.SingletonFactoryImpl;
import amforeas.SingletonFactory;
import amforeas.config.AmforeasConfiguration;
import amforeas.config.DatabaseConfiguration;
import amforeas.exceptions.AmforeasBadRequestException;
import amforeas.handler.AmforeasResultSetHandler;
import amforeas.handler.ResultSetMetaDataHandler;
import amforeas.rest.xstream.Row;
import amforeas.sql.Delete;
import amforeas.sql.DynamicFinder;
import amforeas.sql.Insert;
import amforeas.sql.Select;
import amforeas.sql.Update;
import amforeas.sql.dialect.Dialect;
import amforeas.sql.dialect.DialectFactory;

/**
 * Class in charge of executing SQL statements against a given RDBMS.
 */
public class JDBCExecutor {

    private static final Logger l = LoggerFactory.getLogger(JDBCExecutor.class);

    private final SingletonFactory factory;

    private final JDBCConnectionFactory connectionFactory;
    private final AmforeasConfiguration conf;

    public JDBCExecutor() {
        this.factory = new SingletonFactoryImpl();
        this.connectionFactory = factory.getJDBCConnectionFactory();
        this.conf = factory.getConfiguration();
    }

    public JDBCExecutor(SingletonFactory factory) {
        this.factory = factory;
        this.connectionFactory = factory.getJDBCConnectionFactory();
        this.conf = factory.getConfiguration();
    }

    /**
     * Executes the given {@link amforeas.sql.Delete} object
     * @param delete a {@link amforeas.sql.Delete} instance
     * @return number of records deleted
     * @throws SQLException from the QueryRunner
     * @see org.apache.commons.dbutils.QueryRunner
     */
    public int delete (final Delete delete) throws SQLException {
        l.debug(delete.toString());
        final DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(delete.getTable().getDatabase());
        final QueryRunner run = connectionFactory.getQueryRunner(dbconf);
        final Dialect dialect = DialectFactory.getDialect(dbconf);

        try {
            int deleted = run.update(dialect.toStatementString(delete), AmforeasUtils.parseValue(delete.getId()));
            l.debug("Deleted " + deleted + " records.");
            return deleted;
        } catch (SQLException ex) {
            l.debug(ex.getMessage());
            throw ex;
        }
    }

    /**
     * Executes the given {@link amforeas.sql.Insert} object
     * @param insert a {@link amforeas.sql.Insert} instance
     * @return number of records inserted
     * @throws SQLException from the QueryRunner
     * @see org.apache.commons.dbutils.QueryRunner
     */
    public int insert (final Insert insert) throws SQLException {
        l.debug(insert.toString());

        final DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(insert.getTable().getDatabase());
        final QueryRunner run = connectionFactory.getQueryRunner(dbconf);
        final Dialect dialect = DialectFactory.getDialect(dbconf);

        try {
            int inserted;
            if (insert.getColumns().isEmpty())
                inserted = run.update(dialect.toStatementString(insert));
            else
                inserted = run.update(dialect.toStatementString(insert), AmforeasUtils.parseValues(insert.getValues()));

            l.debug("Inserted " + inserted + " records.");
            return inserted;
        } catch (SQLException ex) {
            l.debug(ex.getMessage());
            throw ex;
        }
    }

    /**
     * Executes the given {@link amforeas.sql.Update} object
     * @param update a {@link amforeas.sql.Update} instance
     * @return a List of {@link amforeas.rest.xstream.Row} with the modified records
     * @throws SQLException from the QueryRunner
     * @see org.apache.commons.dbutils.QueryRunner
     */
    public List<Row> update (final Update update) throws SQLException {
        l.debug(update.toString());

        final DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(update.getTable().getDatabase());
        final QueryRunner run = connectionFactory.getQueryRunner(dbconf);
        final Dialect dialect = DialectFactory.getDialect(dbconf);

        List<Row> results = new ArrayList<Row>();
        try {
            int ret = run.update(dialect.toStatementString(update), AmforeasUtils.parseValues(update.getParameters()));
            if (ret != 0) {
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
     * Executes the given {@link amforeas.sql.Select} object and returns all or one record depending on the value
     * of the allRecords variable
     * @param select a {@link amforeas.sql.Select} instance
     * @param allRecords return all (true) records or one (false) record.
     * @return a List of {@link amforeas.rest.xstream.Row} with the records found by the statement.
     * @throws SQLException from the QueryRunner
     * @see org.apache.commons.dbutils.QueryRunner
     * @see amforeas.handler.AmforeasResultSetHandler
     */
    public List<Row> get (final Select select, final boolean allRecords) throws SQLException {
        l.debug(select.toString());
        List<Row> response = null;

        final DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(select.getTable().getDatabase());
        final QueryRunner run = connectionFactory.getQueryRunner(dbconf);
        final Dialect dialect = DialectFactory.getDialect(dbconf);

        final ResultSetHandler<List<Row>> res = new AmforeasResultSetHandler(allRecords);

        if (select.isAllRecords()) {
            try {
                response = run.query(dialect.toStatementString(select), res);
            } catch (SQLException ex) {
                l.debug(ex.getMessage());
                throw ex;
            }
        } else {
            try {
                response = run.query(
                    dialect.toStatementString(select),
                    res,
                    AmforeasUtils.parseValues(List.of(select.getParameter().getValues())));
            } catch (SQLException ex) {
                l.debug(ex.getMessage());
                throw ex;
            }
        }

        return response;
    }

    /**
     * Executes the given {@link org.amforeas.jdbc.DynamicFinder} object.
     * @param database database name or schema where to execute the {@link org.amforeas.jdbc.DynamicFinder}
     * @param df an instance of {@link org.amforeas.jdbc.DynamicFinder}
     * @param limit an instance of {@link amforeas.jdbc.LimitParam}
     * @param order an instance of {@link amforeas.jdbc.OrderParam}
     * @param params a vararg of Object instances used as parameters for the QueryRunner.
     * @return a List of {@link amforeas.rest.xstream.Row} with the records found by the DynamicFinder.
     * @throws SQLException from the QueryRunner
     * @see org.apache.commons.dbutils.QueryRunner
     * @see amforeas.sql.dialect.Dialect
     */
    public List<Row> find (final String database, final DynamicFinder df, final LimitParam limit, final OrderParam order, Object... params) throws SQLException {
        l.debug(df.getSql());
        l.debug(AmforeasUtils.varargToString(params));

        final DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(database);
        final Dialect dialect = DialectFactory.getDialect(dbconf);
        final String query = dialect.toStatementString(df, limit, order);

        final QueryRunner run = connectionFactory.getQueryRunner(dbconf);
        final ResultSetHandler<List<Row>> res = new AmforeasResultSetHandler(true);
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
     * Executes a given {@link amforeas.sql.Select} object and returns the metadata associated to the results.
     * @param select a {@link amforeas.sql.Select} instance which should only retrieve one result.
     * @return a List of {@link amforeas.rest.xstream.Row} with the metadata obtained 
     * with the {@link amforeas.sql.Select} statement
     * @throws SQLException SQLException from the QueryRunner
     * @see org.apache.commons.dbutils.QueryRunner
     * @see amforeas.handler.ResultSetMetaDataHandler
     */
    public List<Row> getTableMetaData (final Select select) throws SQLException {
        l.debug("Obtaining metadata from table " + select.toString());

        final ResultSetHandler<List<Row>> res = new ResultSetMetaDataHandler();

        final DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(select.getTable().getDatabase());
        final QueryRunner run = connectionFactory.getQueryRunner(dbconf);
        final Dialect dialect = DialectFactory.getDialect(dbconf);

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
     * of {@link amforeas.jdbc.StoredProcedureParam}.
     * @param database database name or schema where to execute the stored procedure or function
     * @param queryName the name of the stored procedure or function. This gets converted to a {call foo()} statement.
     * @param params a List of {@link amforeas.jdbc.StoredProcedureParam} used by the stored procedure or function.
     * @return a List of {@link amforeas.rest.xstream.Row} with the results of the stored procedure (if out parameters are given)
     * or the results of the function.
     * @throws SQLException
     * @throws AmforeasBadRequestException 
     */
    public List<Row> executeQuery (final String database, final String queryName, final List<StoredProcedureParam> params) throws SQLException, AmforeasBadRequestException {
        l.debug("Executing stored procedure " + database + "." + queryName);

        final DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(database);
        final QueryRunner run = connectionFactory.getQueryRunner(dbconf);
        final String call = AmforeasUtils.getCallableStatementCallString(queryName, params.size());
        List<Row> rows = new ArrayList<Row>();

        Connection conn = null;
        CallableStatement cs = null;
        try {
            l.debug("Obtain connection from datasource");
            conn = run.getDataSource().getConnection();

            l.debug("Create callable statement for " + call);
            cs = conn.prepareCall(call);

            l.debug("Add parameters to callable statement");
            final List<StoredProcedureParam> outParams = addParameters(cs, params);

            l.debug("Execute callable statement");
            if (cs.execute()) {
                l.debug("Got a result set " + queryName);
                ResultSet rs = cs.getResultSet();
                AmforeasResultSetHandler handler = new AmforeasResultSetHandler(true);
                rows = handler.handle(rs);
            } else if (!outParams.isEmpty()) {
                l.debug("No result set, but we are expecting OUT values from " + queryName);
                Map<String, String> results = new HashMap<String, String>();
                for (StoredProcedureParam p : outParams) {
                    results.put(p.getName(), cs.getString(p.getIndex())); // thank $deity we only return strings
                }
                rows.add(new Row(0, results));
            }
        } catch (SQLException ex) {
            l.debug(ex.getMessage());
            throw ex;
        } catch (AmforeasBadRequestException ex) {
            l.debug(ex.getMessage());
            throw ex;
        } finally {
            try {
                if (cs != null && !cs.isClosed())
                    cs.close();
            } catch (SQLException ex) {
                l.debug(ex.getMessage());
            }
            try {
                if (conn != null && !conn.isClosed())
                    conn.close();
            } catch (SQLException ex) {
                l.debug(ex.getMessage());
            }
        }
        l.debug("Received " + rows.size() + " results.");
        return rows;
    }

    /**
     * Close all connections to the databases
     */
    public void shutdown () {
        l.debug("Shutting down JDBC connections");
        try {
            connectionFactory.closeConnections();
        } catch (Exception ex) {
            l.warn("Failed to close connection to database?");
            l.debug(ex.getMessage());
        }
    }

    /**
     * For a given database or schema, execute the statement returned by the {@link amforeas.sql.dialect.Dialect} listOfTablesStatement()
     * method and return a List of {@link amforeas.rest.xstream.Row} with all the tables available.
     * @param database the name of the database to query.
     * @return a List of {@link amforeas.rest.xstream.Row} with all the tables available.
     * @throws SQLException 
     */
    public List<Row> getListOfTables (final String database) throws SQLException {
        l.debug("Obtaining the list of tables for the database " + database);
        // if(!conf.allowListTables()){
        // throw JongoJDBCExceptionFactory.getException(database, "Cant read database metadata. Access Denied", JongoJDBCException.ILLEGAL_READ_CODE);
        // }
        final ResultSetHandler<List<Row>> res = new AmforeasResultSetHandler(true);
        final DatabaseConfiguration dbconf = conf.getDatabaseConfiguration(database);
        final Dialect dialect = DialectFactory.getDialect(dbconf);
        final QueryRunner run = connectionFactory.getQueryRunner(dbconf);

        try {
            List<Row> results = run.query(dialect.listOfTablesStatement(), res);
            l.debug("Received " + results.size() + " results.");
            return results;
        } catch (SQLException ex) {
            throw ex;
        }
    }

    /**
     * Utility method which registers in a CallableStatement object the different {@link amforeas.jdbc.StoredProcedureParam}
     * instances in the given list. Returns a List of {@link amforeas.jdbc.StoredProcedureParam} with all the OUT parameters
     * registered in the CallableStatement
     * @param cs the CallableStatement object where the parameters are registered.
     * @param params a list of {@link amforeas.jdbc.StoredProcedureParam}
     * @return a list of OUT {@link amforeas.jdbc.StoredProcedureParam} 
     * @throws SQLException if we fail to register any of the parameters in the CallableStatement
     * @throws AmforeasBadRequestException 
     */
    private List<StoredProcedureParam> addParameters (final CallableStatement cs, final List<StoredProcedureParam> params) throws SQLException, AmforeasBadRequestException {
        final List<StoredProcedureParam> outParams = new ArrayList<StoredProcedureParam>();
        int i = 1;
        for (StoredProcedureParam p : params) {
            final Integer sqlType = p.getSqlType();
            if (p.isOutParameter()) {
                l.debug("Adding OUT parameter " + p.toString());
                cs.registerOutParameter(i++, sqlType);
                outParams.add(p);
            } else {
                l.debug("Adding IN parameter " + p.toString());
                switch (sqlType) {
                    case Types.BIGINT:
                    case Types.INTEGER:
                    case Types.TINYINT:
                        // case Types.NUMERIC:
                        cs.setInt(i++, Integer.valueOf(p.getValue()));
                        break;
                    case Types.DATE:
                        cs.setDate(i++, (Date) AmforeasUtils.parseValue(p.getValue()));
                        break;
                    case Types.TIME:
                        cs.setTime(i++, (Time) AmforeasUtils.parseValue(p.getValue()));
                        break;
                    case Types.TIMESTAMP:
                        cs.setTimestamp(i++, (Timestamp) AmforeasUtils.parseValue(p.getValue()));
                        break;
                    case Types.DECIMAL:
                        cs.setBigDecimal(i++, (BigDecimal) AmforeasUtils.parseValue(p.getValue()));
                        break;
                    case Types.DOUBLE:
                        cs.setDouble(i++, Double.valueOf(p.getValue()));
                        break;
                    case Types.FLOAT:
                        cs.setLong(i++, Long.valueOf(p.getValue()));
                        break;
                    default:
                        cs.setString(i++, p.getValue());
                        break;
                }
            }
        }
        return outParams;
    }

}
