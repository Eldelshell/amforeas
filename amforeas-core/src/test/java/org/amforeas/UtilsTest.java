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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Map;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import amforeas.AmforeasUtils;
import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;

@ExtendWith(MockitoExtension.class)
@Tag("offline-tests")
public class UtilsTest {

    @Test
    public void testAmforeasUtils () {
        AmforeasUtils u = new AmforeasUtils();
        assertNotNull(u);
    }

    @Test
    public void testIsDate () throws ParseException {
        DateTimeFormatter df = ISODateTimeFormat.date();
        DateTime date = df.parseDateTime("2011-01-19");
        assertEquals(date, AmforeasUtils.isDate("2011-01-19"));
        assertEquals(date, AmforeasUtils.isDate("20110119"));

        assertNull(AmforeasUtils.isDate("2011-19-01"));
        assertNull(AmforeasUtils.isDate("2011.01.19"));
        assertNull(AmforeasUtils.isDate("2011"));
        assertNull(AmforeasUtils.isDate(""));
        assertNull(AmforeasUtils.isDate(null));
    }

    @Test
    public void testIsDateTime () {
        DateTimeFormatter df = ISODateTimeFormat.dateTime();
        DateTime date = df.parseDateTime("2011-12-11T12:35:45.200+01:00");
        assertEquals(date, AmforeasUtils.isDateTime("2011-12-11T12:35:45.200+01:00"));
        assertNull(AmforeasUtils.isDateTime("2011-12-11 22:00:00"));
        assertNull(AmforeasUtils.isDateTime(""));
        assertNull(AmforeasUtils.isDateTime(null));
        assertNull(AmforeasUtils.isDateTime("2011-01-19"));
        assertNull(AmforeasUtils.isDateTime("20110119"));
        assertNull(AmforeasUtils.isDateTime("22:00:00"));
        assertNull(AmforeasUtils.isDateTime("12:35:45.200+01:00"));
    }

    @Test
    public void testIsTime () {
        DateTimeFormatter df = ISODateTimeFormat.time();
        DateTime date = df.parseDateTime("12:35:45.000Z");
        assertEquals(date, AmforeasUtils.isTime("12:35:45.000Z"));
        assertEquals(date, AmforeasUtils.isTime("123545.000Z"));
        assertNull(AmforeasUtils.isTime(""));
        assertNull(AmforeasUtils.isTime(null));
        assertNull(AmforeasUtils.isTime("2011-01-19"));
        assertNull(AmforeasUtils.isTime("20110119"));
        assertNull(AmforeasUtils.isTime("22:00:00"));
        assertNull(AmforeasUtils.isTime("2011-12-11T12:35:45.200Z"));
    }

    @Test
    public void testSplitCamelCase () {
        assertEquals(AmforeasUtils.splitCamelCase("nameIsNull"), "name Is Null");
        assertEquals(AmforeasUtils.splitCamelCase("name_idIsNull"), "name_id Is Null");
        assertEquals(AmforeasUtils.splitCamelCase("name_09IsNull"), "name_09 Is Null");
        assertEquals(AmforeasUtils.splitCamelCase("01_09IsNull"), "01_09 Is Null");
        assertEquals(AmforeasUtils.splitCamelCase("01IsNull"), "01 Is Null");
        // This is an invalid usage but the sql will break with this sort of query
        // assertEquals(AmforeasUtils.splitCamelCase("01*.Null"), "01 Is Null");
        assertEquals(AmforeasUtils.splitCamelCase(""), "");
    }

    @Test
    public void parseValue () {
        assertTrue(AmforeasUtils.parseValue("1") instanceof Integer);
        assertTrue(AmforeasUtils.parseValue("1.0") instanceof BigDecimal);
        assertTrue(AmforeasUtils.parseValue(" ") instanceof String);
        assertTrue(AmforeasUtils.parseValue("false") instanceof String);
        assertTrue(AmforeasUtils.parseValue("true") instanceof String);
        assertTrue(AmforeasUtils.parseValue("2011-12-11T12:35:45.200+01:00") instanceof java.sql.Timestamp);
        assertTrue(AmforeasUtils.parseValue("2011-01-19") instanceof java.sql.Date);
        assertTrue(AmforeasUtils.parseValue("12:35:45.200+01:00") instanceof java.sql.Time);
    }

    @Test
    public void testOrderParam () {
        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<>();
        assertEquals(OrderParam.valueOf(formParams).toString(), "id ASC");

        assertThrows(IllegalArgumentException.class, () -> {
            formParams.add("dir", "KKK");
            OrderParam.valueOf(formParams).toString(); // throw Exception!
        });

        assertThrows(IllegalArgumentException.class, () -> {
            formParams.add("dir", "DESC");
            assertEquals(OrderParam.valueOf(formParams).toString(), "id ASC");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            formParams.add("sort", "kkk");
            assertEquals(OrderParam.valueOf(formParams).toString(), "kkk DESC");
        });

        formParams.remove("dir");
        assertEquals(OrderParam.valueOf(formParams).toString(), "kkk ASC");
    }

    @Test
    public void testLimitParam () {
        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<>();
        LimitParam instance = LimitParam.valueOf(formParams);
        assertEquals(instance.getLimit(), Integer.valueOf(25));
        assertEquals(instance.getStart(), Integer.valueOf(0));
        formParams.putSingle("offset", "test");
        instance = LimitParam.valueOf(formParams);
        assertEquals(instance.getLimit(), Integer.valueOf(25));
        assertEquals(instance.getStart(), Integer.valueOf(0));
        formParams.putSingle("offset", "50");
        instance = LimitParam.valueOf(formParams);
        assertEquals(instance.getLimit(), Integer.valueOf(25));
        assertEquals(instance.getStart(), Integer.valueOf(0));
        formParams.putSingle("limit", "100");
        instance = LimitParam.valueOf(formParams);
        assertEquals(instance.getLimit(), Integer.valueOf(100));
        assertEquals(instance.getStart(), Integer.valueOf(50));
        formParams.remove("offset");
        instance = LimitParam.valueOf(formParams);
        assertEquals(instance.getLimit(), Integer.valueOf(100));
        assertEquals(instance.getStart(), Integer.valueOf(0));
    }

    @Test
    public void testGetMD5Base64 () {
        try {
            AmforeasUtils.getMD5Base64(null);
        } catch (IllegalArgumentException e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        String t = AmforeasUtils.getMD5Base64("");
        assertTrue(t.equals("1B2M2Y8AsgTpgAmY7PhCfg=="));
        t = AmforeasUtils.getMD5Base64("xxxxxxxxxxxxxxxxx");
        assertTrue(t.equals("PvgoOWefBe8mDjrJgt6TzQ=="));
    }

    @Test
    public void getOctetLength () {
        try {
            AmforeasUtils.getOctetLength(null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        Integer t = AmforeasUtils.getOctetLength("");
        assertTrue(t.equals(0));
        t = AmforeasUtils.getOctetLength("xxxxxxxxxxxxxxxxx");
        assertTrue(t.equals(17));
    }

    @Test
    public void testHashMapOf () {
        try {
            AmforeasUtils.hashMapOf(null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        MultivaluedMap<String, String> m = new MultivaluedHashMap<>();
        Map<String, String> map = AmforeasUtils.hashMapOf(m);
        assertTrue(map.isEmpty());

        m.add("t1", "kkk");
        map = AmforeasUtils.hashMapOf(m);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());

        m.add("t2", "");
        map = AmforeasUtils.hashMapOf(m);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
    }

    @Test
    public void testGetCallableStatementCallString () {
        String k = AmforeasUtils.getCallableStatementCallString("test", 7);
        assertEquals("{CALL test(?,?,?,?,?,?,?)}", k);
        k = AmforeasUtils.getCallableStatementCallString("test", 0);
        assertEquals("{CALL test()}", k);
        try {
            AmforeasUtils.getCallableStatementCallString("", 0);
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            AmforeasUtils.getCallableStatementCallString(null, 0);
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
}
