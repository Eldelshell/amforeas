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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Amforeas. If not, see <http://www.gnu.org/licenses/>.
 */
package org.amforeas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.amforeas.mocks.AmforeasMapConverter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.thoughtworks.xstream.XStream;
import amforeas.jdbc.StoredProcedureParam;
import amforeas.rest.xstream.ErrorResponse;
import amforeas.rest.xstream.SuccessResponse;
import amforeas.rest.xstream.Row;

/**
 *
 * @author Alejandro Ayuso
 */
@Tag("offline-tests")
public class XmlXstreamTest {

    public XmlXstreamTest() {}

    @BeforeAll
    public static void setUpClass () throws Exception {}

    @AfterAll
    public static void tearDownClass () throws Exception {}

    @Test
    public void testSuccessToXML () {
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

        SuccessResponse s = new SuccessResponse("test", rows);
        printXMLObject(s, "AmforeasSuccess.xml");
        //        System.out.println(s.toXML());
        //        s = successFromXML(s.toXML());
        //        Assert.assertTrue(s.isSuccess());
    }

    //    @Test
    //    public void testErrorToXML () {
    //        AmforeasError s = new AmforeasError("grrr", 500, "grrrr error");
    //        System.out.println(s.toXML());
    //        s = errorFromXML(s.toXML());
    //        Assert.assertFalse(s.isSuccess());
    //        s = new AmforeasError("grrr", new SQLException("grrr", "GR101", 54333));
    //        System.out.println(s.toXML());
    //        s = errorFromXML(s.toXML());
    //        Assert.assertFalse(s.isSuccess());
    //    }

    public static SuccessResponse successFromXML (final String xml) {
        XStream xStreamInstance = new XStream();
        xStreamInstance.setMode(XStream.NO_REFERENCES);
        xStreamInstance.autodetectAnnotations(false);
        xStreamInstance.alias("response", SuccessResponse.class);
        xStreamInstance.alias("row", Row.class);
        xStreamInstance.registerConverter(new AmforeasMapConverter());
        xStreamInstance.aliasAttribute(Row.class, "roi", "roi");
        return (SuccessResponse) xStreamInstance.fromXML(xml);
    }

    public static ErrorResponse errorFromXML (final String xml) {
        XStream xStreamInstance = new XStream();
        xStreamInstance.setMode(XStream.NO_REFERENCES);
        xStreamInstance.autodetectAnnotations(false);
        xStreamInstance.alias("response", ErrorResponse.class);
        return (ErrorResponse) xStreamInstance.fromXML(xml);
    }

    @Test
    public void testJAX () throws Exception {
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
        SuccessResponse s = new SuccessResponse("test", rows);

        List<StoredProcedureParam> ps = new ArrayList<StoredProcedureParam>();
        ps.add(new StoredProcedureParam("car_id", "1", false, 1, "INTEGER"));
        ps.add(new StoredProcedureParam("dfgdf", "", true, 2, "VARCHAR"));
        StoredProcedureParam p = new StoredProcedureParam("car_id", "1", false, 1, "INTEGER");
        final ObjectMapper mapper = new ObjectMapper();
        String k = mapper.writeValueAsString(ps);
        System.out.println(k);
        printXMLObject(p, "");
        List<StoredProcedureParam> ret =
            new ObjectMapper().readValue(k, new TypeReference<List<StoredProcedureParam>>() {});
        System.out.println(ret);


    }

    public String printJSONObject (final Object obj, final String message) {
        System.out.println(message);
        try {
            final ObjectMapper mapper = new ObjectMapper();
            String k = mapper.writeValueAsString(obj);
            System.out.println(k);
            return k;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void prettyPrintJSONObject (final Object obj, final String message) {
        System.out.println(message);
        try {
            final ObjectMapper mapper = new ObjectMapper();
            String k = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            System.out.println(k);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void printXMLObject (final Object obj, final String message) {
        System.out.println(message);
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(obj, System.out);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
