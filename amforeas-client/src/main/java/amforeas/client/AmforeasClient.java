package amforeas.client;

import java.util.List;
import java.util.Optional;
import org.apache.http.NameValuePair;
import amforeas.jdbc.StoredProcedureParam;

public interface AmforeasClient<T> {

    /**
     * Obtain meta information about the alias
     * @return
     */
    public Optional<T> meta ();

    /**
     * Obtain meta information about the resource (table or view)
     * @param resource - the resource name
     * @return
     */
    public Integer meta (String resource);

    /**
     * Obtains all records of a resource.
     * SELECT * FROM resource;
     * @param resource - the resource name
     * @return
     */
    public Optional<T> getAll (String resource);

    /**
     * Obtains all records of a resource that match the given id
     * SELECT * FROM resource WHERE id = ?;
     * @param resource - the resource name
     * @param id - id of the resource
     * @return
     */
    public Optional<T> get (String resource, String id);

    /**
     * Obtains all records of a resource that match the given primary key
     * SELECT * FROM resource WHERE ${pk} = ?;
     * @param resource - the resource name
     * @param pk - name of the primary key if it's not "id"
     * @param id - id of the resource
     * @return
     */
    public Optional<T> get (String resource, String pk, String id);

    /**
     * Obtains all records of a resource that matche the given name on the given column
     * SELECT * FROM resource WHERE ${col} = ${arg}
     * @param resource - the resource name
     * @param col - the col name
     * @param arg - the col value
     * @return
     */
    public Optional<T> find (String resource, String col, String arg);

    /**
     * Use a dynamic query to find resources.
     * @param resource - the resource name
     * @param query - the query like findAllByNameEquals or findByAgeNotNull
     * @param args - values to provide to the dynamic query if required (max 2)
     * @return
     */
    public Optional<T> query (String resource, String query, String... args);

    /**
     * Adds the given entity to the resource
     * @param resource - the resource name
     * @param json - the entity in JSON format
     * @return
     */
    public Optional<T> add (String resource, String json);

    /**
     * Adds the given entity to the resource
     * @param resource - the resource name
     * @param params - the entity in urlencoded format
     * @return
     */
    public Optional<T> add (String resource, List<NameValuePair> params);

    /**
     * Update the entity with the given fields
     * @param resource - the resource name
     * @param id - the id of the resource
     * @param json - the new data in JSON format
     * @return
     */
    public Optional<T> update (String resource, String id, String json);

    /**
     * Update the entity with the given fields
     * @param resource - the resource name
     * @param pk - name of the primary key if it's not "id"
     * @param id - the id of the resource
     * @param json - the new data in JSON format
     * @return
     */
    public Optional<T> update (String resource, String pk, String id, String json);

    /**
     * Delete the entity with the given fields
     * @param resource - the resource name
     * @param id - the id of the resource
     * @return
     */
    public Optional<T> delete (String resource, String id);

    /**
     * Delete the entity with the given fields
     * @param resource - the resource name
     * @param pk - name of the primary key if it's not "id"
     * @param id - the id of the resource
     * @return
     */
    public Optional<T> delete (String resource, String pk, String id);

    /**
     * Calls a stored procedure or function
     * @param function - function name
     * @param params - params
     * @return
     */
    public Optional<T> call (String function, StoredProcedureParam... params);

    /**
     * Performs an HTTP GET for the given request
     * @param request - a {@link RequestParams} with the paramters
     * @return
     */
    public Optional<T> get (final RequestParams request);

    /**
     * Performs an HTTP POST for the given request
     * @param request - a {@link RequestParams} with the paramters
     * @param contentType - if JSON or urlencoded
     * @return
     */
    public Optional<T> post (final RequestParams request, String contentType);

    /**
     * Performs an HTTP PUT for the given request
     * @param request - a {@link RequestParams} with the paramters
     * @param contentType - if JSON or urlencoded
     * @return
     */
    public Optional<T> put (final RequestParams request, String contentType);

    /**
     * Performs an HTTP DELETE for the given request
     * @param request - a {@link RequestParams} with the paramters
     * @return
     */
    public Optional<T> delete (final RequestParams request);
}
