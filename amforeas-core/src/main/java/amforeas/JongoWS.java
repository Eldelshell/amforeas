/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
 *
 * This file is part of Amforeas.
 * Amforeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * Amforeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Amforeas.  If not, see <http://www.gnu.org/licenses/>.
 */

package amforeas;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Public RESTful webservice which allows CRUD operations on a given resource.
 * @author Alejandro Ayuso 
 */
public interface AmforeasWS {
    
    /**
     * REST gateway for database metadata
     * @param database name of the database we want to access
     * @return depends on database implementations but probably, a list of the
     * tables defined in the database or schema.
     */
    public Response dbMeta(String database);
    
    /**
     * REST gateway for resource metadata. A resource can be a table, a view, etc.
     * @param database name of the database the resource belongs to
     * @param resource name of the resource we want to access
     * @return metadata about the resource, like its fields.
     */
    public Response resourceMeta(String database, String resource);
    
    /**
     * Obtains the requested record from the given resource
     * @param database name of the database the resource belongs to
     * @param resource name of the resource we want to access
     * @param pk optional field which indicates the primary key column name. Defaults to "id"
     * @param id the primary key value of the record we want to access
     * @return the record if it's found, or a 404 if it's not.
     */
    public Response get(String database, String resource, String pk, String id);
    
    /**
     * Obtain all the records from a given resource.
     * @param database name of the database the resource belongs to
     * @param resource name of the resource we want to access
     * @param pk optional field which indicates the primary key column name. Defaults to "id"
     * @return all the records if found, or a 404 if it's not.
     */
    public Response getAll(String database, String resource, String pk);
    
    /**
     * Finds a record from the given resource which matches the given argument in the given column.
     * @param database name of the database the resource belongs to
     * @param resource name of the resource we want to access
     * @param col name of the column the record must match
     * @param arg value in the column the record must match.
     * @return the record if it's found, or a 404 if it's not.
     */
    public Response find(String database, String resource, String col, String arg);
    
    /**
     * Finds all records from the given resource which matches the given query with the given list of arguments.
     * @param database name of the database the resource belongs to
     * @param resource name of the resource we want to access
     * @param query a {@link org.amforeas.jdbc.DynamicFinder} query
     * @param args a list of arguments to be given to the {@link org.amforeas.jdbc.DynamicFinder}
     * @return all the records which match the given {@link org.amforeas.jdbc.DynamicFinder}
     */
    public Response findBy(String database, String resource, String query, List<String> args);
    
    /**
     * Creates a record in the given resource with values from a JSON representation.
     * @param database name of the database the resource belongs to
     * @param resource name of the resource we want to access
     * @param pk optional field which indicates the primary key column name. Defaults to "id"
     * @param jsonRequest JSON representation of the values we want to insert. For example:
     * {"name":"foo", "age":40}
     * @return a {@link amforeas.rest.xstream.AmforeasSuccess} response with the number of records inserted and a
     * CREATED HTTP Code.
     */
    public Response insert(String database, String resource, String pk, String jsonRequest);
    
    /**
     * Creates a record in the given resource with values from a x-www-form-urlencoded representation as given
     * by a HTML form.
     * @param database name of the database the resource belongs to
     * @param resource name of the resource we want to access
     * @param pk optional field which indicates the primary key column name. Defaults to "id"
     * @param formParams a x-www-form-urlencoded representation of the values we want to insert.
     * @return a {@link amforeas.rest.xstream.AmforeasSuccess} response with the number of records inserted and a
     * CREATED HTTP Code. If an error occurs a BAD REQUEST or NO CONTENT errors are returned.
     */
    public Response insert(String database, String resource, String pk, MultivaluedMap<String, String> formParams);
    
    /**
     * Updates a record in the given resource with values from a JSON representation.
     * @param database name of the database the resource belongs to
     * @param resource name of the resource we want to access
     * @param pk optional field which indicates the primary key column name. Defaults to "id"
     * @param id the primary key value of the record we want to update.
     * @param jsonRequest JSON representation of the values we want to insert. For example:
     * {"name":"foo", "age":40}
     * @return a {@link amforeas.rest.xstream.AmforeasSuccess} response with the updated record.
     */
    public Response update(String database, String resource, String pk, String id, String jsonRequest);
    
    /**
     * Deletes a record in the given resource with the given id.
     * @param database name of the database the resource belongs to
     * @param resource name of the resource we want to access
     * @param pk optional field which indicates the primary key column name. Defaults to "id"
     * @param id the primary key value of the record we want to delete.
     * @return a {@link amforeas.rest.xstream.AmforeasSuccess} response with the number of records deleted and a
     * OK HTTP Code. If an error occurs a BAD REQUEST or NO CONTENT errors are returned.
     */
    public Response delete(String database, String resource, String pk, String id);
    
    /**
     * Calls the given function or stored procedure with the given JSON parameters.
     * @param database  name of the database the function or stored procedure belongs to
     * @param query name of the function or stored procedure
     * @param jsonRequest IN and OUT parameters in JSON format. For example:
     * [
     *  {"value":2010, "name":"year", "outParameter":false, "type":"INTEGER", "index":1},
     *  {"name":"out_total", "outParameter":true, "type":"INTEGER", "index":2}
     * ]
     * @return 
     */
    public Response storedProcedure(String database, String query, String jsonRequest);
    
    /**
     * Returns statistics about amforeas usage. {@link amforeas.rest.xstream.Usage}
     * @return statistics of amforeas usage.
     */
    public Response getJongoStatistics();
    
}
