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
package org.amforeas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import amforeas.AmforeasUtils;
import amforeas.jdbc.StoredProcedureParam;
import amforeas.rest.xstream.ErrorResponse;
import amforeas.rest.xstream.HeadResponse;
import amforeas.rest.xstream.Row;
import amforeas.rest.xstream.SuccessResponse;

/**
 * Test JSON and XML marshalling/unmarshalling works
 */
@Tag("offline-tests")
public class XmlXstreamTest {

    private static final Logger l = LoggerFactory.getLogger(XmlXstreamTest.class);

    public XmlXstreamTest() {}

    @BeforeAll
    public static void setUpClass () throws Exception {
        l.debug("Running XmlXstreamTest");
    }

    @AfterAll
    public static void tearDownClass () throws Exception {}

    @Test
    public void testSuccessToXML () {
        assertSuccessXMLResponse(printXMLObject(new SuccessResponse()));
        assertSuccessXMLResponse(printXMLObject(new SuccessResponse("test", new ArrayList<Row>())));
        assertSuccessXMLResponse(printXMLObject(new SuccessResponse("test", getBasicRows())));
        assertSuccessXMLResponse(printXMLObject(new SuccessResponse("test", getBasicRows(), Status.OK)));
    }

    @Test
    public void testErrorToXML () {
        assertErrorXMLResponse(printXMLObject(new ErrorResponse()));
        assertErrorXMLResponse(printXMLObject(new ErrorResponse("test", Status.BAD_REQUEST)));
        assertErrorXMLResponse(printXMLObject(new ErrorResponse("test", Status.BAD_REQUEST, "my message")));
        assertErrorXMLResponse(printXMLObject(new ErrorResponse("testsql", new SQLException("reason", "SQLState", 1))));
    }

    @Test
    public void testHeadToXML () {
        assertHeadXMLResponse(printXMLObject(new HeadResponse()));
        assertHeadXMLResponse(printXMLObject(new HeadResponse("test", new ArrayList<Row>())));
        assertHeadXMLResponse(printXMLObject(new HeadResponse("test", getBasicRows())));
        assertHeadXMLResponse(printXMLObject(new HeadResponse("test", getBasicRows(), Status.OK)));
    }

    @Test
    public void assertJSONResponse () {
        assertSuccessJSONResponse(printJSONObject(new SuccessResponse()));
        assertSuccessJSONResponse(printJSONObject(new SuccessResponse("test", new ArrayList<Row>())));
        assertSuccessJSONResponse(printJSONObject(new SuccessResponse("test", getBasicRows())));
        assertSuccessJSONResponse(printJSONObject(new SuccessResponse("test", getBasicRows(), Status.OK)));
    }

    @Test
    public void testErrorToJSON () {
        assertErrorJSONResponse(printJSONObject(new ErrorResponse()));
        assertErrorJSONResponse(printJSONObject(new ErrorResponse("test", Status.BAD_REQUEST)));
        assertErrorJSONResponse(printJSONObject(new ErrorResponse("test", Status.BAD_REQUEST, "my message")));
        assertErrorJSONResponse(printJSONObject(new ErrorResponse("testsql", new SQLException("reason", "SQLState", 1))));
    }

    @Test
    public void testHeadToJSON () {
        assertHeadJSONResponse(printJSONObject(new HeadResponse()));
        assertHeadJSONResponse(printJSONObject(new HeadResponse("test", new ArrayList<Row>())));
        assertHeadJSONResponse(printJSONObject(new HeadResponse("test", getBasicRows())));
        assertHeadJSONResponse(printJSONObject(new HeadResponse("test", getBasicRows(), Status.OK)));
    }

    @Test
    public void testStoredProcedureParam () throws Exception {
        StoredProcedureParam p = new StoredProcedureParam("car_id", "1", false, 1, "INTEGER");
        String pJson = printJSONObject(p);
        assertEquals(pJson, "{\"value\":\"1\",\"name\":\"car_id\",\"outParameter\":false,\"type\":\"INTEGER\",\"index\":1}");
        StoredProcedureParam p2 = new ObjectMapper().readValue(pJson, StoredProcedureParam.class);
        assertEquals(p2, p);

        List<StoredProcedureParam> ps = new ArrayList<>();
        ps.add(new StoredProcedureParam("car_id", "1", false, 1, "INTEGER"));
        ps.add(new StoredProcedureParam("dfgdf", "", true, 2, "VARCHAR"));

        final ObjectMapper mapper = new ObjectMapper();
        String k = mapper.writeValueAsString(ps);
        List<StoredProcedureParam> ret = AmforeasUtils.getStoredProcedureParamsFromJSON(k);
        assertFalse(ret.isEmpty());
        assertEquals(ret.size(), 2);
    }

    private void assertXMLResponse (final String xml) {
        // l.debug(json);
        assertTrue(xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response>") && xml.endsWith("</response>"));
    }

    private void assertJSONResponse (final String json) {
        // l.debug(json);
        assertTrue(json.startsWith("{") && json.endsWith("}"));
    }

    private void assertSuccessXMLResponse (final String xml) {
        assertXMLResponse(xml);
        SuccessResponse obj = successFromXML(xml);
        assertTrue(obj instanceof SuccessResponse);
        assertTrue(obj.getStatus().equals(Status.OK));
    }

    private void assertErrorXMLResponse (final String xml) {
        assertXMLResponse(xml);
        ErrorResponse obj = errorFromXML(xml);
        assertTrue(obj instanceof ErrorResponse);
        assertTrue(obj.getStatus().equals(Status.BAD_REQUEST));
    }

    private void assertHeadXMLResponse (final String xml) {
        assertXMLResponse(xml);
        HeadResponse obj = headFromXML(xml);
        assertTrue(obj instanceof HeadResponse);
        assertTrue(obj.getStatus().equals(Status.OK));
    }

    private void assertSuccessJSONResponse (final String json) {
        assertJSONResponse(json);
        try {
            SuccessResponse obj = new ObjectMapper().readValue(json, SuccessResponse.class);
            assertTrue(obj instanceof SuccessResponse);
            assertTrue(obj.getStatus().equals(Status.OK));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void assertErrorJSONResponse (final String json) {
        assertJSONResponse(json);
        try {
            ErrorResponse obj = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertTrue(obj instanceof ErrorResponse);
            assertTrue(obj.getStatus().equals(Status.BAD_REQUEST));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void assertHeadJSONResponse (final String json) {
        assertJSONResponse(json);
        try {
            HeadResponse obj = new ObjectMapper().readValue(json, HeadResponse.class);
            assertTrue(obj instanceof HeadResponse);
            assertTrue(obj.getStatus().equals(Status.OK));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Row> getBasicRows () {
        Map<String, String> m1 = new HashMap<String, String>();
        List<Row> rows = new ArrayList<Row>();

        m1.put("id", "1");
        m1.put("name", "test1");
        m1.put("age", "56");
        rows.add(new Row(1, m1));

        m1 = new HashMap<String, String>();
        m1.put("id", "2");
        m1.put("name", "test2");
        m1.put("age", "526");

        rows.add(new Row(1, m1));
        return rows;
    }

    public static SuccessResponse successFromXML (final String xml) {
        XStream xStreamInstance = new XStream();
        xStreamInstance.setMode(XStream.NO_REFERENCES);
        xStreamInstance.autodetectAnnotations(false);
        xStreamInstance.alias("response", SuccessResponse.class);
        xStreamInstance.alias("row", Row.class);
        xStreamInstance.alias("cells", HashMap.class);
        xStreamInstance.alias("roi", Integer.class);
        xStreamInstance.registerConverter(new AmforeasMapConverter());
        xStreamInstance.addImplicitCollection(SuccessResponse.class, "rows", Row.class);
        return (SuccessResponse) xStreamInstance.fromXML(xml);
    }

    public static ErrorResponse errorFromXML (final String xml) {
        XStream xStreamInstance = new XStream();
        xStreamInstance.setMode(XStream.NO_REFERENCES);
        xStreamInstance.autodetectAnnotations(false);
        xStreamInstance.alias("response", ErrorResponse.class);
        return (ErrorResponse) xStreamInstance.fromXML(xml);
    }

    public static HeadResponse headFromXML (final String xml) {
        XStream xStreamInstance = new XStream();
        xStreamInstance.setMode(XStream.NO_REFERENCES);
        xStreamInstance.autodetectAnnotations(false);
        xStreamInstance.alias("response", HeadResponse.class);
        xStreamInstance.alias("row", Row.class);
        xStreamInstance.registerConverter(new AmforeasMapConverter());
        xStreamInstance.addImplicitCollection(HeadResponse.class, "rows", Row.class);
        return (HeadResponse) xStreamInstance.fromXML(xml);
    }

    public String printJSONObject (final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String prettyPrintJSONObject (final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String printXMLObject (final Object obj) {
        return printXMLObject(obj, false);
    }

    public String printXMLObject (final Object obj, final boolean pretty) {
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, pretty);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            marshaller.marshal(obj, os);
            return os.toString("UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    static class AmforeasMapConverter implements Converter {

        @Override
        public void marshal (Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
            Map<String, Object> map = (Map<String, Object>) o;
            for (String key : map.keySet()) {
                Object val = map.get(key);
                writer.startNode(key.toLowerCase());
                if (val != null) {
                    writer.setValue(val.toString());
                } else {
                    writer.setValue("");
                }

                writer.endNode();
            }
        }

        @Override
        public Object unmarshal (HierarchicalStreamReader reader, UnmarshallingContext uc) {
            Map<String, Object> map = new HashMap<String, Object>();
            MultivaluedMap<String, String> mv = new MultivaluedHashMap<>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                mv.add(reader.getNodeName(), reader.getValue());
                map.put(reader.getNodeName(), reader.getValue());
                reader.moveUp();
            }

            if (uc.getRequiredType().equals(MultivaluedMap.class)) {
                return mv;
            } else {
                return map;
            }

        }

        @Override
        public boolean canConvert (Class type) {
            return type.equals(HashMap.class) || type.equals(MultivaluedMap.class);
        }

    }
}
