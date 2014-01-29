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
package jongo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import jongo.config.JongoConfiguration;
import jongo.exceptions.JongoBadRequestException;
import jongo.jdbc.JDBCExecutor;
import jongo.jdbc.LimitParam;
import jongo.jdbc.OrderParam;
import jongo.jdbc.StoredProcedureParam;
import jongo.rest.xstream.JongoError;
import jongo.rest.xstream.JongoHead;
import jongo.rest.xstream.JongoResponse;
import jongo.rest.xstream.JongoSuccess;
import jongo.rest.xstream.Row;
import jongo.sql.Delete;
import jongo.sql.DynamicFinder;
import jongo.sql.Insert;
import jongo.sql.Select;
import jongo.sql.SelectParam;
import jongo.sql.Table;
import jongo.sql.Update;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the RESTful operations. Serves as a backend for the {@link jongo.JongoWS} implementations.
 * @author Alejandro Ayuso 
 */
public class RestController {
    
    private static final Logger l = LoggerFactory.getLogger(RestController.class);
    private static final JongoConfiguration conf = JongoConfiguration.instanceOf();
    
    private final String alias;
    private final String database;
    
    /**
     * Instantiates a new controller for the given database/schema if this exists
     * @param alias the name of the database/schema to work with
     * @throws IllegalArgumentException if the database/schema name is blank, empty or null
     */
    public RestController(String alias){
        if(StringUtils.isBlank(alias))
            throw new IllegalArgumentException("Alias name can't be blank, empty or null");
        
        this.alias = alias;
        this.database = conf.getDatabaseConfigurationForAlias(alias).getDatabase();
    }
    
    /**
     * Obtains a list of tables for the given database/schema and returns a {@link jongo.rest.xstream.JongoSuccess}
     * response.
     * @return  a {@link jongo.rest.xstream.JongoSuccess} or a {@link jongo.rest.xstream.JongoError}
     */
    public JongoResponse getDatabaseMetadata(){
        l.debug("Obtaining metadata for " + database);
        JongoResponse response = null;
        
        List<Row> results = null;
        try {
            results = JDBCExecutor.getListOfTables(database);
        } catch (Throwable ex){
            response = handleException(ex, database);
        }
        
        if(response == null){
            response = new JongoSuccess(database, results);
        }
        
        return response;
    }
    
    /**
     * Obtains a list of columns for the given resource and returns a {@link jongo.rest.xstream.JongoSuccess}
     * response.
     * @param table name of the resource to obtain the metadata from
     * @return a {@link jongo.rest.xstream.JongoSuccess} or a {@link jongo.rest.xstream.JongoError}
     */
    public JongoResponse getResourceMetadata(final String table){
        l.debug("Obtaining metadata for " + table);
        
        Table t;
        try{
            t = new Table(database, table);
        }catch (IllegalArgumentException e){
            l.debug("Failed to generate select " + e.getMessage());
            return new JongoError(table, Response.Status.BAD_REQUEST, e.getMessage());
        }
        
        Select select = new Select(t).setLimitParam(new LimitParam(1));
        
        JongoResponse response = null;
        List<Row> results = null;
        try {
            results = JDBCExecutor.getTableMetaData(select);
        } catch (Throwable ex){
            response = handleException(ex, table);
        }
        
        if(results == null && response == null){
            response = new JongoError(table, Response.Status.NO_CONTENT);
        }
        
        if(response == null){
            response = new JongoHead(table, results);
        }
        
        return response;
    }
    
    /**
     * Retrieves all resources from a given table ordered and limited.
     * @param table the table or view to query
     * @param limit a LimitParam object with the limit values
     * @param order order an OrderParam object with the ordering values.
     * @return Returns a JongoResponse with the values of the resource. If the resource is not available an error
     * if the table is empty, we return a SuccessResponse with no values.
     */
    public JongoResponse getAllResources(final String table, final LimitParam limit, final OrderParam order){
        l.debug("Geting all resources from " + alias + "." + table);
        
        Table t;
        try{
            t = new Table(database, table);
        }catch (IllegalArgumentException e){
            l.debug("Failed to generate select " + e.getMessage());
            return new JongoError(table, Response.Status.BAD_REQUEST, e.getMessage());
        }
        
        Select s = new Select(t).setLimitParam(limit).setOrderParam(order);
        
        JongoResponse response = null;
        List<Row> results = null;
        try{
            results = JDBCExecutor.get(s, true);
        } catch (Throwable ex){
            response = handleException(ex, table);
        }
        
        if(results == null && response == null){
            response = new JongoError(table, Response.Status.NOT_FOUND);
        }
        
        if(response == null){
            response = new JongoSuccess(table, results);
        }
        
        return response;
    }
    
    /**
     * Retrieves one resource for the given id. 
     * @param table the table or view to query
     * @param col the column defined to be used in the query. Defaults to "id"
     * @param arg the value of the col.
     * @param limit a LimitParam object with the limit values
     * @param order an OrderParam object with the ordering values.
     * @return Returns a JongoResponse with the values of the resource. If the resource is not available an error is returned.
     */
    public JongoResponse getResource(final String table, final String col, final String arg, final LimitParam limit, final OrderParam order){
        l.debug("Geting resource from " + alias + "." + table + " with id " + arg);
        
        Table t;
        try{
            t = new Table(database, table);
        }catch (IllegalArgumentException e){
            l.debug("Failed to generate select " + e.getMessage());
            return new JongoError(table, Response.Status.BAD_REQUEST, e.getMessage());
        }
        
        Select select = new Select(t).setParameter(new SelectParam(col, arg)).setLimitParam(limit).setOrderParam(order);
        
        JongoResponse response = null;
        List<Row> results = null;
        try{
            results = JDBCExecutor.get(select, false);
        } catch (Throwable ex){
            response = handleException(ex, table);
        }
        
        if((results == null || results.isEmpty()) && response == null){
            response = new JongoError(table, Response.Status.NOT_FOUND);
        }
        
        if(response == null){
            response = new JongoSuccess(table, results);
        }
        
        return response;
    }
    
    /**
     * Retrieves all resources for the given column and value. 
     * @param table the table or view to query
     * @param col the column defined to be used in the query. Defaults to "id"
     * @param arg the value of the col.
     * @param limit a LimitParam object with the limit values
     * @param order an OrderParam object with the ordering values.
     * @return Returns a JongoResponse with the values of the resources. If the resources are not available an error is returned.
     */
    public JongoResponse findResources(final String table, final String col, final String arg, final LimitParam limit, final OrderParam order){
        l.debug("Geting resource from " + alias + "." + table + " with id " + arg);
        
        if(StringUtils.isEmpty(arg) || StringUtils.isEmpty(col))
            return new JongoError(table, Response.Status.BAD_REQUEST, "Invalid argument");
        
        Table t;
        try{
            t = new Table(database, table);
        }catch (IllegalArgumentException e){
            l.debug("Failed to generate select " + e.getMessage());
            return new JongoError(table, Response.Status.BAD_REQUEST, e.getMessage());
        }
        
        Select select = new Select(t).setParameter(new SelectParam(col, arg)).setLimitParam(limit).setOrderParam(order);
        
        JongoResponse response = null;
        List<Row> results = null;
        try{
            results = JDBCExecutor.get(select, true);
        } catch (Throwable ex){
            response = handleException(ex, table);
        }
        
        if((results == null || results.isEmpty()) && response == null){
            response = new JongoError(table, Response.Status.NOT_FOUND);
        }
        
        if(response == null){
            response = new JongoSuccess(table, results);
        }
        
        return response;
    }
    
    /**
     * Generates an instance of {@link jongo.sql.Insert} for the given JSON arguments and calls the 
     * insertResource(Insert) method.
     * @param resource the resource or view where to insert the record.
     * @param pk optional field which indicates the primary key column name. Defaults to "id"
     * @param jsonRequest JSON representation of the values we want to insert. For example:
     * {"name":"foo", "age":40}
     * @return a {@link jongo.rest.xstream.JongoSuccess} or a {@link jongo.rest.xstream.JongoError}
     */
    public JongoResponse insertResource(final String resource, final String pk, final String jsonRequest){
        l.debug("Insert new " + alias + "." + resource + " with JSON values: " + jsonRequest);
        
        JongoResponse response;
        
        try {
            Map<String, String> params = JongoUtils.getParamsFromJSON(jsonRequest);
            response = insertResource(resource, pk, params);
        } catch (JongoBadRequestException ex){
            l.info("Failed to parse JSON arguments " + ex.getMessage());
            response = new JongoError(resource, Response.Status.BAD_REQUEST, ex.getMessage());
        }
        
        return response;
    }
    
    /**
     * Generates an instance of {@link jongo.sql.Insert} for the given x-www-form-urlencoded arguments and calls the 
     * insertResource(Insert) method.
     * @param resource the resource or view where to insert the record.
     * @param pk optional field which indicates the primary key column name. Defaults to "id"
     * @param formParams a x-www-form-urlencoded representation of the values we want to insert.
     * @return a {@link jongo.rest.xstream.JongoSuccess} or a {@link jongo.rest.xstream.JongoError}
     */
    public JongoResponse insertResource(final String resource, final String pk, final Map<String, String> formParams){
        l.debug("Insert new " + alias + "." + resource + " with values: " + formParams);
        
        JongoResponse response;
        Table t;
        try{
            t = new Table(database, resource);
        }catch (IllegalArgumentException e){
            l.debug("Failed to generate Insert " + e.getMessage());
            return new JongoError(resource, Response.Status.BAD_REQUEST, e.getMessage());
        }
        
        Insert insert = new Insert(t).setColumns(formParams);
        response = insertResource(insert);
        
        return response;
    }
    
    /**
     * Calls the {@link jongo.jdbc.JDBCExecutor} insert method with the 
     * given {@link jongo.sql.Insert} instance and handles errors.
     * @param insert a {@link jongo.sql.Insert} instance
     * @return a {@link jongo.rest.xstream.JongoSuccess} or a {@link jongo.rest.xstream.JongoError}
     */
    private JongoResponse insertResource(Insert insert){
        JongoResponse response = null;
        int result = 0;
        try {
            result = JDBCExecutor.insert(insert);
        } catch (Throwable ex){
            response = handleException(ex, insert.getTable().getName());
        }
        
        if(result == 0 && response == null){
            response = new JongoError(null, Response.Status.NO_CONTENT);
        }

        if(response == null){
            List<Row> results = new ArrayList<Row>();
            results.add(new Row(0));
            response = new JongoSuccess(null, results, Response.Status.CREATED);
        }
        return response;
    }
    
    /**
     * Creates an instance of {@link jongo.sql.Update}, calls 
     * the {@link jongo.jdbc.JDBCExecutor} update method and handles errors
     * @param resource the resource or view where to insert the record.
     * @param pk optional field which indicates the primary key column name. Defaults to "id"
     * @param jsonRequest JSON representation of the values we want to update. For example:
     * {"name":"foo", "age":40}
     * @return a {@link jongo.rest.xstream.JongoSuccess} or a {@link jongo.rest.xstream.JongoError}
     */
    public JongoResponse updateResource(final String resource, final String pk, final String id, final String jsonRequest){
        l.debug("Update record " + id + " in table " + alias + "." + resource + " with values: " + jsonRequest);
        JongoResponse response = null;
        
        List<Row> results = null;
        
        Table t;
        try{
            t = new Table(database, resource, pk);
        }catch (IllegalArgumentException e){
            l.debug("Failed to generate update " + e.getMessage());
            return new JongoError(resource, Response.Status.BAD_REQUEST, e.getMessage());
        }
        
        Update update = new Update(t).setId(id);
        try {
            update.setColumns(JongoUtils.getParamsFromJSON(jsonRequest));
            results = JDBCExecutor.update(update);
        } catch (Throwable ex){
            response = handleException(ex, resource);
        }
        
        if((results == null || results.isEmpty()) && response == null){
            response =  new JongoError(resource, Response.Status.NO_CONTENT);
        }

        if(response == null){
            response = new JongoSuccess(resource, results, Response.Status.OK);
        }
        
        return response;
    }
    
    /**
     * Creates an instance of {@link jongo.sql.Delete}, calls 
     * the {@link jongo.jdbc.JDBCExecutor} delete method and handles errors
     * @param resource the resource or view where to insert the record.
     * @param pk optional field which indicates the primary key column name. Defaults to "id"
     * @param id unique pk identifier of the record to delete.
     * @return a {@link jongo.rest.xstream.JongoSuccess} or a {@link jongo.rest.xstream.JongoError}
     */
    public JongoResponse deleteResource(final String resource, final String pk, final String id){
        l.debug("Delete record " + id + " from table " + alias + "." + resource);
        
        Table t;
        try{
            t = new Table(database, resource, pk);
        }catch (IllegalArgumentException e){
            l.debug("Failed to generate delete " + e.getMessage());
            return new JongoError(resource, Response.Status.BAD_REQUEST, e.getMessage());
        }
        
        Delete delete = new Delete(t).setId(id);
        JongoResponse response = null;
        int result = 0;
        try {
            result = JDBCExecutor.delete(delete);
        } catch (Throwable ex){
            response = handleException(ex, resource);
        }
        
        if(result == 0 && response == null){
            response = new JongoError(resource, Response.Status.NO_CONTENT);
        }

        if(response == null){
            List<Row> results = new ArrayList<Row>();
            results.add(new Row(0));
            response = new JongoSuccess(resource, results, Response.Status.OK);
        }
        return response;
    }
    
    /**
     * Generates a {@link org.jongo.jdbc.DynamicFinder} from the given parameters and calls
     * the {@link jongo.jdbc.JDBCExecutor} find method and handles errors
     * @param resource the resource or view where to insert the record.
     * @param query a {@link org.jongo.jdbc.DynamicFinder} query
     * @param values a list of arguments to be given to the {@link org.jongo.jdbc.DynamicFinder}
     * @param limit a {@link jongo.jdbc.LimitParam} instance.
     * @param order a {@link jongo.jdbc.OrderParam} instance.
     * @return a {@link jongo.rest.xstream.JongoSuccess} or a {@link jongo.rest.xstream.JongoError}
     */
    public JongoResponse findByDynamicFinder(final String resource, final String query, final List<String> values, final LimitParam limit, final OrderParam order){
        l.debug("Find resource from " + alias + "." + resource + " with " + query);
        
        if(values == null)
            throw new IllegalArgumentException("Invalid null argument");
        
        if(query == null)
            return new JongoError(resource, Response.Status.BAD_REQUEST, "Invalid query");
        
        JongoResponse response = null;
        List<Row> results = null;
        
        if(values.isEmpty()){
            try{
                DynamicFinder df = DynamicFinder.valueOf(resource, query);
                results = JDBCExecutor.find(database, df, limit, order);
            } catch (Throwable ex){
                response = handleException(ex, resource);
            }
        }else{
            try{
                DynamicFinder df = DynamicFinder.valueOf(resource, query, values.toArray(new String []{}));
                results = JDBCExecutor.find(database, df, limit, order, JongoUtils.parseValues(values));
            } catch (Throwable ex){
                response = handleException(ex, resource);
            }
        }
        
        if((results == null || results.isEmpty()) && response == null){
            response = new JongoError(resource, Response.Status.NOT_FOUND, "No results for " + query);
        }
        
        if(response == null){
            response =  new JongoSuccess(resource, results);
        }
        
        return response;
    }
    
    /**
     * Generates a List of {@link jongo.jdbc.StoredProcedureParam} and executes
     * the {@link jongo.jdbc.JDBCExecutor} executeQuery method with the given JSON parameters.
     * @param query name of the function or stored procedure
     * @param json IN and OUT parameters in JSON format. For example:
     * [
     *  {"value":2010, "name":"year", "outParameter":false, "type":"INTEGER", "index":1},
     *  {"name":"out_total", "outParameter":true, "type":"INTEGER", "index":2}
     * ]
     * @return a {@link jongo.rest.xstream.JongoSuccess} or a {@link jongo.rest.xstream.JongoError}
     */
    public JongoResponse executeStoredProcedure(final String query, final String json){
        l.debug("Executing Stored Procedure " + query);
        
        List<StoredProcedureParam> params;
        try {
            params = JongoUtils.getStoredProcedureParamsFromJSON(json);
        } catch (JongoBadRequestException ex) {
            return handleException(ex, query);
        }
        
        JongoResponse response = null;
        List<Row> results = null;
        try {
            results = JDBCExecutor.executeQuery(database, query, params);
        } catch (Throwable ex){
            response = handleException(ex, query);
        }
        
        if(response == null){
            response = new JongoSuccess(query, results);
        }
        return response;
    }
    
    /**
     * Method in charge of handling the possible exceptions thrown by the JDBCExecutor or any other
     * operation. The current implementation handles SQLException, JongoBadRequestException &
     * IllegalArgumentException to return different errors. For any other exception 
     * a {@link jongo.rest.xstream.JongoError} with a 500 status code is returned.
     * @param t the exception to handle.
     * @param resource the name of the resource which is throwing the exception.
     * @return a {@link jongo.rest.xstream.JongoError} with different error codes depending
     * on the exception being handled. If we can't handle the exception, a 500 error code is used.
     */
    private JongoResponse handleException(final Throwable t, final String resource){
        JongoResponse response;
        StringBuilder b;
        if(t instanceof SQLException){
            SQLException ex = (SQLException)t;
            b = new StringBuilder("Received a SQLException ");
            b.append(ex.getMessage());
            b.append(" state [");
            b.append(ex.getSQLState());
            b.append("] & code [");
            b.append(ex.getErrorCode());
            b.append("]");
            l.debug(b.toString());
            response = new JongoError(resource, ex);
        }else if(t instanceof JongoBadRequestException){
            b = new StringBuilder("Received a JongoBadRequestException ");
            b.append(t.getMessage());
            l.debug(b.toString());
            response = new JongoError(resource, Response.Status.BAD_REQUEST, t.getMessage());
        }else if(t instanceof IllegalArgumentException){
            b = new StringBuilder("Received an IllegalArgumentException ");
            b.append(t.getMessage());
            l.debug(b.toString());
            response = new JongoError(resource, Response.Status.BAD_REQUEST, t.getMessage());
        }else{
            b = new StringBuilder("Received an Unhandled Exception ");
            b.append(t.getMessage());
            l.error(b.toString());
            response = new JongoError(resource, Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
