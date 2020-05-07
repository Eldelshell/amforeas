package amforeas.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;

public class RequestParamsTest {

    private static final String root = "a";
    private static final String alias = "b";
    private static final String resource = "c";

    @Test
    public void testRead () {
        assertEquals(RequestParams.builder(resource).build().getPath(root, alias), "a/b/c");
        assertEquals(RequestParams.builder(resource).id("1234").build().getPath(root, alias), "a/b/c/1234");
        assertEquals(RequestParams.builder(resource).id("1234").build().getPrimaryKeyValue(), "1234");
        assertEquals(RequestParams.builder(resource).id("1234").build().getPrimaryKey(), "id");
        assertEquals(RequestParams.builder(resource).key("t_id").id("1234").build().getPrimaryKey(), "t_id");
        assertEquals(RequestParams.builder(resource).column("age").value("1").build().getPath(root, alias), "a/b/c/age/1");
    }

    @Test
    public void testRead_errors () {
        assertThrows(IllegalStateException.class, () -> RequestParams.builder(resource).value("x"));
    }

    @Test
    public void testDynamicQuery () {
        RequestParams rp = RequestParams.builder(resource).dynamicQuery("findByName").addQueryParam("bar").build();
        assertEquals(rp.getPath(root, alias), "a/b/c/dynamic/findByName");
        assertEquals(rp.getParametersAsQueryParams(), "args=bar");

        rp = RequestParams.builder(resource).dynamicQuery("findAllByNameAndAge").addQueryParam("bar").addQueryParam("2").build();
        assertEquals(rp.getPath(root, alias), "a/b/c/dynamic/findAllByNameAndAge");
        assertEquals(rp.getParametersAsQueryParams(), "args=bar&args=2");

        rp = RequestParams.builder(resource)
            .dynamicQuery("findAllByNameAndAge")
            .addQueryParam("bar")
            .addQueryParam("2")
            .page(2)
            .sortBy("age", "asc")
            .build();

        assertEquals(rp.getPath(root, alias), "a/b/c/dynamic/findAllByNameAndAge");
        assertEquals(rp.getParametersAsQueryParams(), "page=2&sort=age&dir=ASC&args=bar&args=2");
    }

    @Test
    public void testDynamicQuery_errors () {
        assertThrows(IllegalArgumentException.class, () -> RequestParams.builder(resource).dynamicQuery("invalid"));
        assertThrows(IllegalArgumentException.class, () -> RequestParams.builder(resource).dynamicQuery("Find"));
        assertThrows(IllegalStateException.class, () -> RequestParams.builder(resource).addQueryParam("bar"));
    }

    @Test
    public void testInsert () throws UnsupportedEncodingException, JsonProcessingException {
        RequestParams rp = RequestParams.builder(resource).insert("foo", "bar").build();
        assertEquals(rp.getPath(root, alias), "a/b/c");
        assertNotNull(rp.getFormBody());
        assertNotNull(rp.getJSONBody());

        assertThrows(IllegalStateException.class, () -> RequestParams.builder(resource).build().getFormBody());
        assertThrows(IllegalStateException.class, () -> RequestParams.builder(resource).build().getJSONBody());
    }

    @Test
    public void testUpdate () throws UnsupportedEncodingException, JsonProcessingException {
        RequestParams rp = RequestParams.builder(resource).id("1").update("foo", "bar").build();
        assertEquals(rp.getPath(root, alias), "a/b/c/1");
        assertNotNull(rp.getFormBody());
        assertNotNull(rp.getJSONBody());

        assertThrows(IllegalStateException.class, () -> RequestParams.builder(resource).update("foo", "bar").build());
        assertThrows(IllegalStateException.class, () -> RequestParams.builder(resource).id("foo").build().getFormBody());
        assertThrows(IllegalStateException.class, () -> RequestParams.builder(resource).id("foo").build().getJSONBody());
    }
}
