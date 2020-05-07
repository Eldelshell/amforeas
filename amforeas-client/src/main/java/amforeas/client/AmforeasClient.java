package amforeas.client;

import java.util.List;
import java.util.Optional;
import org.apache.http.NameValuePair;
import amforeas.jdbc.StoredProcedureParam;
import amforeas.rest.xstream.AmforeasResponse;

public interface AmforeasClient {

    public Optional<AmforeasResponse> meta ();

    public Integer meta (String resource);

    public Optional<AmforeasResponse> getAll (String resource);

    public Optional<AmforeasResponse> get (String resource, String id);

    public Optional<AmforeasResponse> get (String resource, String pk, String id);

    public Optional<AmforeasResponse> find (String resource, String col, String arg);

    public Optional<AmforeasResponse> query (String resource, String query, String... args);

    public Optional<AmforeasResponse> add (String resource, String json);

    public Optional<AmforeasResponse> add (String resource, String pk, String json);

    public Optional<AmforeasResponse> add (String resource, List<NameValuePair> params);

    public Optional<AmforeasResponse> add (String resource, String pk, List<NameValuePair> params);

    public Optional<AmforeasResponse> update (String resource, String id, String json);

    public Optional<AmforeasResponse> update (String resource, String pk, String id, String json);

    public Optional<AmforeasResponse> delete (String resource, String id);

    public Optional<AmforeasResponse> delete (String resource, String pk, String id);

    public Optional<AmforeasResponse> call (String function, StoredProcedureParam... params);

    public Optional<AmforeasResponse> get (final RequestParams request);

    public Optional<AmforeasResponse> post (final RequestParams request, String contentType);

    public Optional<AmforeasResponse> put (final RequestParams request, String contentType);

    public Optional<AmforeasResponse> delete (final RequestParams request);
}
