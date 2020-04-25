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
package org.amforeas;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;

import amforeas.JongoUtils;
import amforeas.config.DatabaseConfiguration;
import amforeas.config.JongoConfiguration;
import amforeas.enums.JDBCDriver;
import amforeas.exceptions.StartupException;
import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Alejandro Ayuso 
 */
public class UtilsTest {
    
    @Before
    public void setUp(){
        System.setProperty("environment","demo");
    }
    
    @Test
    public void testJongoUtils(){
        JongoUtils u = new JongoUtils(); 
        assertNotNull(u);
    }
    
    @Test
    public void testIsDate() throws ParseException{
        DateTimeFormatter df = ISODateTimeFormat.date();
        DateTime date = df.parseDateTime("2011-01-19");
        assertEquals(date, JongoUtils.isDate("2011-01-19"));
        assertEquals(date, JongoUtils.isDate("20110119"));
        
        assertNull(JongoUtils.isDate("2011-19-01"));
        assertNull(JongoUtils.isDate("2011.01.19"));
        assertNull(JongoUtils.isDate("2011"));
        assertNull(JongoUtils.isDate(""));
        assertNull(JongoUtils.isDate(null));
    }
    
    @Test
    public void testIsDateTime(){
        DateTimeFormatter df = ISODateTimeFormat.dateTime();
        DateTime date = df.parseDateTime("2011-12-11T12:35:45.200+01:00");
        assertEquals(date, JongoUtils.isDateTime("2011-12-11T12:35:45.200+01:00"));
        assertNull(JongoUtils.isDateTime("2011-12-11 22:00:00"));
        assertNull(JongoUtils.isDateTime(""));
        assertNull(JongoUtils.isDateTime(null));
        assertNull(JongoUtils.isDateTime("2011-01-19"));
        assertNull(JongoUtils.isDateTime("20110119"));
        assertNull(JongoUtils.isDateTime("22:00:00"));
        assertNull(JongoUtils.isDateTime("12:35:45.200+01:00"));
    }
    
    @Test
    public void testIsTime(){
        DateTimeFormatter df = ISODateTimeFormat.time();
        DateTime date = df.parseDateTime("12:35:45.000Z");
        assertEquals(date, JongoUtils.isTime("12:35:45.000Z"));
        assertEquals(date, JongoUtils.isTime("123545.000Z"));
        assertNull(JongoUtils.isTime(""));
        assertNull(JongoUtils.isTime(null));
        assertNull(JongoUtils.isTime("2011-01-19"));
        assertNull(JongoUtils.isTime("20110119"));
        assertNull(JongoUtils.isTime("22:00:00"));
        assertNull(JongoUtils.isTime("2011-12-11T12:35:45.200Z"));
    }
    
    @Test
    public void testSplitCamelCase(){
        assertEquals(JongoUtils.splitCamelCase("nameIsNull"), "name Is Null");
        assertEquals(JongoUtils.splitCamelCase("name_idIsNull"), "name_id Is Null");
        assertEquals(JongoUtils.splitCamelCase("name_09IsNull"), "name_09 Is Null");
        assertEquals(JongoUtils.splitCamelCase("01_09IsNull"), "01_09 Is Null");
        assertEquals(JongoUtils.splitCamelCase("01IsNull"), "01 Is Null");
//        This is an invalid usage but the sql will break with this sort of query
//        assertEquals(JongoUtils.splitCamelCase("01*.Null"), "01 Is Null");
        assertEquals(JongoUtils.splitCamelCase(""), "");
    }
    
    @Test
    public void parseValue(){
        assertTrue(JongoUtils.parseValue("1") instanceof Integer);
        assertTrue(JongoUtils.parseValue("1.0") instanceof BigDecimal);
        assertTrue(JongoUtils.parseValue(" ") instanceof String);
        assertTrue(JongoUtils.parseValue("false") instanceof String);
        assertTrue(JongoUtils.parseValue("true") instanceof String);
        assertTrue(JongoUtils.parseValue("2011-12-11T12:35:45.200+01:00") instanceof java.sql.Timestamp);
        assertTrue(JongoUtils.parseValue("2011-01-19") instanceof java.sql.Date);
        assertTrue(JongoUtils.parseValue("12:35:45.200+01:00") instanceof java.sql.Time);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testOrderParam(){
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        assertEquals(OrderParam.valueOf(formParams).toString(), "id ASC");
        formParams.add("dir", "KKK");
        OrderParam.valueOf(formParams).toString(); // throw Exception!
        formParams.add("dir", "DESC");
        assertEquals(OrderParam.valueOf(formParams).toString(), "id ASC");
        formParams.add("sort", "kkk");
        assertEquals(OrderParam.valueOf(formParams).toString(), "kkk DESC");
        formParams.remove("dir");
        assertEquals(OrderParam.valueOf(formParams).toString(), "kkk ASC");
    }
    
    @Test
    public void testLimitParam(){
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        LimitParam instance = LimitParam.valueOf(formParams);
        assertEquals(instance.getLimit(), new Integer(25));
        assertEquals(instance.getStart(), new Integer(0));
        formParams.putSingle("offset", "test");
        instance = LimitParam.valueOf(formParams);
        assertEquals(instance.getLimit(), new Integer(25));
        assertEquals(instance.getStart(), new Integer(0));
        formParams.putSingle("offset", "50");
        instance = LimitParam.valueOf(formParams);
        assertEquals(instance.getLimit(), new Integer(25));
        assertEquals(instance.getStart(), new Integer(0));
        formParams.putSingle("limit", "100");
        instance = LimitParam.valueOf(formParams);
        assertEquals(instance.getLimit(), new Integer(100));
        assertEquals(instance.getStart(), new Integer(50));
        formParams.remove("offset");
        instance = LimitParam.valueOf(formParams);
        assertEquals(instance.getLimit(), new Integer(100));
        assertEquals(instance.getStart(), new Integer(0));
    }
    
    @Test
    public void testGetMD5Base64(){
        try{
            JongoUtils.getMD5Base64(null);
        }catch(IllegalArgumentException e){
            assertTrue(e instanceof IllegalArgumentException);
        }
        String t = JongoUtils.getMD5Base64("");
        assertTrue(t.equals("1B2M2Y8AsgTpgAmY7PhCfg=="));
        t = JongoUtils.getMD5Base64("xxxxxxxxxxxxxxxxx");
        assertTrue(t.equals("PvgoOWefBe8mDjrJgt6TzQ=="));
    }
    
    @Test
    public void getOctetLength(){
        try{
            JongoUtils.getOctetLength(null);
        }catch(Exception e){
            assertTrue(e instanceof IllegalArgumentException);
        }
        Integer t = JongoUtils.getOctetLength("");
        assertTrue(t.equals(0));
        t = JongoUtils.getOctetLength("xxxxxxxxxxxxxxxxx");
        assertTrue(t.equals(17));
    }
    
    @Test
    public void testHashMapOf(){
        try{
            JongoUtils.hashMapOf(null);
        }catch(Exception e){
            assertTrue(e instanceof IllegalArgumentException);
        }
        
        MultivaluedMap<String, String> m = new MultivaluedMapImpl();
        Map<String, String> map = JongoUtils.hashMapOf(m);
        assertTrue(map.isEmpty());
        
        m.add("t1", "kkk");
        map = JongoUtils.hashMapOf(m);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        
        m.add("t2", "");
        map = JongoUtils.hashMapOf(m);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
    }
    
    @Test
    public void testLoadConfiguration() throws StartupException{
        System.setProperty("environment", "demo");
        JongoConfiguration conf = JongoUtils.loadConfiguration();
        assertTrue(conf.isDemoModeActive());
        
        System.setProperty("environment", "");
        conf = JongoUtils.loadConfiguration();
        assertTrue(conf.isDemoModeActive());
    }

    @Test
    public void testGetCallableStatementCallString(){
        String k = JongoUtils.getCallableStatementCallString("test", 7);
        assertEquals("{CALL test(?,?,?,?,?,?,?)}", k);
        k = JongoUtils.getCallableStatementCallString("test", 0);
        assertEquals("{CALL test()}", k);
        try{
            JongoUtils.getCallableStatementCallString("", 0);
        }catch(IllegalArgumentException e){
            assertNotNull(e.getMessage());
        }
        
        try{
            JongoUtils.getCallableStatementCallString(null, 0);
        }catch(IllegalArgumentException e){
            assertNotNull(e.getMessage());
        }
    }
}
