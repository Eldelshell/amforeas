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

import java.util.*;
import javax.ws.rs.core.Response;

import amforeas.JongoUtils;
import amforeas.RestController;
import amforeas.config.JongoConfiguration;
import amforeas.demo.Demo;
import amforeas.exceptions.JongoBadRequestException;
import amforeas.exceptions.StartupException;
import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;
import amforeas.rest.xstream.JongoError;
import amforeas.rest.xstream.JongoHead;
import amforeas.rest.xstream.JongoSuccess;
import amforeas.rest.xstream.Row;
import junit.framework.Assert;
import org.amforeas.mocks.UserMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alejandro Ayuso 
 */
public class RestControllerTest {
    
    private static final Logger l = LoggerFactory.getLogger(RestControllerTest.class);
    
    RestController controller = new RestController("demo1");
    LimitParam limit = new LimitParam();
    OrderParam order = new OrderParam();
    
    @BeforeClass
    public static void setUp() throws StartupException{
        System.setProperty("environment", "demo");
        JongoUtils.loadConfiguration();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        System.setProperty("environment", "demo");
        JongoConfiguration configuration = JongoUtils.loadConfiguration();
        Demo.destroyDemoDatabases(configuration.getDatabases());
        JongoConfiguration.reset();
    }
    
    @Test
    public void testRestController(){
        try{
            new RestController(null);
        }catch(Exception e){
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }
        try{
            new RestController("");
        }catch(Exception e){
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }
        try{
            new RestController(" ");
        }catch(Exception e){
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }
        
        try{
            new RestController("noway");
        }catch(Exception e){
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }
    }
    
    @Test
    public void testGetDatabaseMetadata(){
        JongoSuccess r = (JongoSuccess)controller.getDatabaseMetadata();
        testSuccessResponse(r, Response.Status.OK, 8);
    }
    
    @Test
    public void testGetResourceMetadata(){
        JongoHead r = (JongoHead)controller.getResourceMetadata("users");
        Assert.assertEquals(Response.Status.OK, r.getStatus());
        Assert.assertTrue(r.isSuccess());
        Assert.assertEquals(6, r.getRows().size());
        
        r = (JongoHead)controller.getResourceMetadata("MAKER_STATS_2010");
        Assert.assertEquals(Response.Status.OK, r.getStatus());
        Assert.assertTrue(r.isSuccess());
        Assert.assertEquals(3, r.getRows().size());
        
        JongoError err = (JongoError)controller.getResourceMetadata(null);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.getResourceMetadata("");
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.getResourceMetadata("this table doesnt exists");
        testErrorResponse(err, Response.Status.BAD_REQUEST, "42501", new Integer(-5501));
    }
    
    @Test
    public void testReadResource(){
        
        JongoSuccess r = (JongoSuccess)controller.getResource("users", "id", "0", limit, order);
        testSuccessResponse(r, Response.Status.OK, 1);
        
        r = (JongoSuccess)controller.getResource("users", "name", "foo", limit, order);
        testSuccessResponse(r, Response.Status.OK, 1);
        
        r = (JongoSuccess)controller.getResource("users", "birthday", "1992-01-15", limit, order);
        testSuccessResponse(r, Response.Status.OK, 1);
        
        r = (JongoSuccess)controller.getResource("users", "age", "33", limit, order);
        testSuccessResponse(r, Response.Status.OK, 1);
        
        r = (JongoSuccess)controller.getResource("users", "credit", "32.5", limit, order);
        testSuccessResponse(r, Response.Status.OK, 1);
        
//        r = (JongoSuccess)controller.getResource("users", "id", "", limit, order);
//        testSuccessResponse(r, Response.Status.OK, 1);
        
//        r = (JongoSuccess)controller.getResource("users", "id", null, limit, order);
//        testSuccessResponse(r, Response.Status.OK, 1);
        
//        r = (JongoSuccess)controller.getResource("users", "", null, limit, order);
//        testSuccessResponse(r, Response.Status.OK, 1);
        
//        r = (JongoSuccess)controller.getResource("users", null, null, limit, order);
//        testSuccessResponse(r, Response.Status.OK, 1);

        JongoError err = (JongoError)controller.getResource("", "id", "0", limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.getResource(null, null, null, limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        // fails if we try for a non-existing resource.
        err = (JongoError)controller.getResource("users", "id", "1999", limit, order);
        testErrorResponse(err, Response.Status.NOT_FOUND, null, null);
        
        err = (JongoError)controller.getResource("users", "id", "not an integer", limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, "22018", -3438);
        
        err = (JongoError)controller.getResource("users", "name", "1999", limit, order);
        testErrorResponse(err, Response.Status.NOT_FOUND, null, null);
        
        // test a table with a custom column
        order.setColumn("cid");
        r = (JongoSuccess)controller.getResource("car", "cid", "1", limit, order);
        testSuccessResponse(r, Response.Status.OK, 1);
        
        r = (JongoSuccess)controller.getResource("car", "transmission", "Automatic", limit, order);
        testSuccessResponse(r, Response.Status.OK, 1);
    }
    
    @Test
    public void testReadAllResources(){
        limit = new LimitParam();
        order = new OrderParam();
        JongoSuccess r = (JongoSuccess)controller.getAllResources("maker", limit, order);
        testSuccessResponse(r, Response.Status.OK, 25);
        
        order.setColumn("cid");
        r = (JongoSuccess)controller.getAllResources("car", limit, order);
        testSuccessResponse(r, Response.Status.OK, 3);
        
        order.setColumn("id");
        JongoError err = (JongoError)controller.getAllResources("no_exists", limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, "42501", new Integer(-5501));
        
        err = (JongoError)controller.getAllResources("", limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.getAllResources(null, limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        r = (JongoSuccess)controller.getAllResources("empty", limit, order);
        testSuccessResponse(r, Response.Status.OK, 0);
        
        // test a view
        r = (JongoSuccess)controller.getAllResources("maker_stats_2010", new LimitParam(1000),  new OrderParam("month"));
        testSuccessResponse(r, Response.Status.OK, 744);
    }
    
//    @Test
    public void testFindByDynamicFinder(){
        testDynamicFinder("users", "findAllByAgeBetween", 5, "18", "99");
        testDynamicFinder("users", "findAllByBirthdayBetween", 3, "1992-01-01", "1992-12-31");
        
        order.setColumn("cid");
        testDynamicFinder("car", "findAllByFuelIsNull", 1);
        testDynamicFinder("car", "findAllByFuelIsNotNull", 2);
        
        order.setColumn("id");
        testDynamicFinder("users", "findAllByCreditGreaterThan", 1, "0");
        testDynamicFinder("users", "findAllByCreditGreaterThanEquals", 2, "0");
        testDynamicFinder("users", "findAllByCreditLessThanEquals", 1, "0");
        testDynamicFinder("sales_stats", "findAllByLast_updateBetween", 6, "2000-01-01T00:00:00.000Z", "2000-06-01T23:55:00.000Z");
        
        JongoError err = (JongoError)controller.findByDynamicFinder("users","findAllByCreditLessThan", Arrays.asList(new String [] {"0"}), limit, order);
        testErrorResponse(err, Response.Status.NOT_FOUND, null, null);
        
        err = (JongoError)controller.findByDynamicFinder("users","findAllByCreditLessThhhhan", Arrays.asList(new String [] {"0"}), limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.findByDynamicFinder("users","findAllByCreditLessThhhhan", Arrays.asList(new String [] {""}), limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.findByDynamicFinder("users","", Arrays.asList(new String [] {"0"}), limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.findByDynamicFinder("users", null, Arrays.asList(new String [] {"0"}), limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.findByDynamicFinder("","", Arrays.asList(new String [] {"0"}), limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.findByDynamicFinder(null,"", Arrays.asList(new String [] {"0"}), limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.findByDynamicFinder("",null, Arrays.asList(new String [] {"0"}), limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.findByDynamicFinder(null,null, Arrays.asList(new String [] {"0"}), limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.findByDynamicFinder("users","findAllByCreditLessThan", new ArrayList<String>(), limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, "S1000", -424);
        
        try{
        controller.findByDynamicFinder("users","findAllByCreditLessThan", null, limit, order);
        }catch(Exception e){
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }
        
        limit = new LimitParam(1000); order = new OrderParam("month");
        testDynamicFinder("maker_stats_2010", "findAllByMakerLikeAndMonthLessThanEquals", 24, "A%", "4");
    }
    
    @Test
    public void testCreateResource(){
        UserMock newMock = UserMock.getRandomInstance();
        JongoSuccess r = (JongoSuccess)controller.insertResource("users", "id", newMock.toJSON());
        testSuccessResponse(r, Response.Status.CREATED, 1);
        
        r = (JongoSuccess)controller.getResource("users", "name", newMock.name, limit, order);
        testSuccessResponse(r, Response.Status.OK, 1);
        
        newMock = UserMock.getRandomInstance();
        r = (JongoSuccess)controller.insertResource("users", "id", newMock.toMap());
        testSuccessResponse(r, Response.Status.CREATED, 1);
        
        r = (JongoSuccess)controller.getResource("users", "name", newMock.name, limit, order);
        testSuccessResponse(r, Response.Status.OK, 1);
        
        JongoError err = (JongoError)controller.insertResource("users", "id", "");
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.insertResource("users", "id", new HashMap<String,String>());
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.insertResource("", "id", new HashMap<String,String>());
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.insertResource(null, "id", new HashMap<String,String>());
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.insertResource("users", "", new HashMap<String,String>());
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.insertResource("users", null, new HashMap<String,String>());
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        newMock = UserMock.getRandomInstance();
        Map<String, String> wrongUserParams = newMock.toMap();
        wrongUserParams.put("birthday", "0000"); // in wrong format
        err = (JongoError)controller.insertResource("users", "id", wrongUserParams);
        testErrorResponse(err, Response.Status.BAD_REQUEST, "42561", new Integer(-5561));
        
        wrongUserParams = newMock.toMap();
        wrongUserParams.put("age", null); // age can be null
        r = (JongoSuccess)controller.insertResource("users", "id", wrongUserParams);
        testSuccessResponse(r, Response.Status.CREATED, 1);
        
        wrongUserParams = newMock.toMap();
        wrongUserParams.put("age", ""); // age can't be empty
        err = (JongoError)controller.insertResource("users", "id", wrongUserParams);
        testErrorResponse(err, Response.Status.BAD_REQUEST, "22018", new Integer(-3438));
        
        wrongUserParams = newMock.toMap();
        wrongUserParams.put("name", null); // name can't be null
        err = (JongoError)controller.insertResource("users", "id", wrongUserParams);
        testErrorResponse(err, Response.Status.BAD_REQUEST, "23502", new Integer(-10));
        
        wrongUserParams = newMock.toMap();
        wrongUserParams.put("name", ""); // name can be empty
        r = (JongoSuccess)controller.insertResource("users", "id", wrongUserParams);
        testSuccessResponse(r, Response.Status.CREATED, 1);
        r = (JongoSuccess)controller.getResource("users", "name", newMock.name, limit, order);
        testSuccessResponse(r, Response.Status.OK, 1);
    }
    
    @Test
    public void testUpdateResource(){
        JongoSuccess r = (JongoSuccess)controller.updateResource("users", "id", "0", "{\"age\":\"90\"}");
        testSuccessResponse(r, Response.Status.OK, 1);
        
        JongoError err = (JongoError)controller.updateResource("users", "id", "0", "{\"age\":\"\"}"); // age can't be empty
        testErrorResponse(err, Response.Status.BAD_REQUEST, "22018", new Integer(-3438));
        
        err = (JongoError)controller.updateResource("users", "id", "0", "{\"age\":}"); // invalid json
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.updateResource("users", "id", "0", "{\"age\":\"90\", \"birthday\":00X0}"); // invalid date
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.updateResource("users", "id", "0", "{\"age\":\"90\", \"birthday\":\"00X0\"}"); // invalid date
        testErrorResponse(err, Response.Status.BAD_REQUEST, "22007", new Integer(-3407));
        
        err = (JongoError)controller.updateResource(null, "id", "0", "{\"age\":\"90\"}");
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.updateResource("users", "id", "9999", "{\"age\":\"90\"}");
        testErrorResponse(err, Response.Status.NO_CONTENT, null, null);
        
        r = (JongoSuccess)controller.updateResource("car", "cid", "0", "{\"model\":\"Test$%&·$&%·$/()=?¿Model\"}"); //custom id
        testSuccessResponse(r, Response.Status.OK, 1);
        
        r = (JongoSuccess)controller.updateResource("maker_stats_2010", "month", "3", "{\"sales\":0}");
        testSuccessResponse(r, Response.Status.OK, 1);
    }
    
    @Test
    public void testRemoveResource(){
        UserMock newMock = UserMock.getRandomInstance();
        JongoSuccess r = (JongoSuccess)controller.insertResource("users", "id", newMock.toJSON());
        testSuccessResponse(r, Response.Status.CREATED, 1);
        
        r = (JongoSuccess)controller.getResource("users", "name", newMock.name, limit, order);
        testSuccessResponse(r, Response.Status.OK, 1);
        
        String id = getId(r, "id");
        Assert.assertNotNull(id);
        
        r = (JongoSuccess)controller.deleteResource("users", "id", id);
        testSuccessResponse(r, Response.Status.OK, 1);
        
        JongoError err = (JongoError)controller.deleteResource("users", "id", "");
        testErrorResponse(err, Response.Status.BAD_REQUEST, "22018", new Integer(-3438));
        
        err = (JongoError)controller.deleteResource("users", "id", null);
        testErrorResponse(err, Response.Status.NO_CONTENT, null, null);
        
        err = (JongoError)controller.deleteResource("users", "", "32");
        testErrorResponse(err, Response.Status.NO_CONTENT, null, null);
        
        err = (JongoError)controller.deleteResource(null, "", "32");
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
    }
    
    @Test
    public void testFindResources(){
        JongoSuccess r = (JongoSuccess)controller.findResources("comments","car_id","0", limit, order);
        testSuccessResponse(r, Response.Status.OK, 2);
        
        r = (JongoSuccess)controller.findResources("comments","car_id","2", limit, order);
        testSuccessResponse(r, Response.Status.OK, 1);
        
        r = (JongoSuccess)controller.findResources("maker_stats_2010","maker","FIAT", limit, new OrderParam("month"));
        testSuccessResponse(r, Response.Status.OK, 12);
        
        JongoError err = (JongoError)controller.findResources("comments","car_id","1", limit, order);
        testErrorResponse(err, Response.Status.NOT_FOUND, null, null);
        
        err = (JongoError)controller.findResources("comments","car_id_grrr","2", limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, "42501", new Integer(-5501));
        
        err = (JongoError)controller.findResources("comments","car_id","", limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.findResources("comments","","0", limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
        
        err = (JongoError)controller.findResources("","id","0", limit, order);
        testErrorResponse(err, Response.Status.BAD_REQUEST, null, null);
    }
    
    @Test
    public void testSimpleFunction() throws JongoBadRequestException{
        JongoSuccess r = (JongoSuccess)controller.executeStoredProcedure("simpleStoredProcedure", "[]");
        testSuccessResponse(r, Response.Status.OK, 1);
        
    }
    
    @Test
    public void testSimpleStoredProcedure()throws JongoBadRequestException{
        String json = "[{\"value\":4,\"name\":\"car_id\",\"outParameter\":false,\"type\":\"INTEGER\",\"index\":1},{\"value\":\"This is a comment\",\"name\":\"comment\",\"outParameter\":false,\"type\":\"VARCHAR\",\"index\":2}]";
        JongoUtils.getStoredProcedureParamsFromJSON(json);
        JongoSuccess r = (JongoSuccess)controller.executeStoredProcedure("insert_comment", json);
        testSuccessResponse(r, Response.Status.OK, 0);
    }
    
    @Test
    public void testComplexStoredProcedure() throws JongoBadRequestException{
        String json = "[{\"value\":2010,\"name\":\"in_year\",\"outParameter\":false,\"type\":\"INTEGER\",\"index\":1},{\"name\":\"out_total\",\"outParameter\":true,\"type\":\"INTEGER\",\"index\":2}]";
        JongoUtils.getStoredProcedureParamsFromJSON(json);
        JongoSuccess r = (JongoSuccess)controller.executeStoredProcedure("get_year_sales", json);
        testSuccessResponse(r, Response.Status.OK, 1);
        Assert.assertEquals("12", r.getRows().get(0).getCells().get("out_total"));
    }
    
    private void testErrorResponse(JongoError err, Response.Status expectedStatus, String expectedSqlState, Integer expectedSqlCode){
        Assert.assertEquals(expectedStatus, err.getStatus());
        Assert.assertNotNull(err.getMessage());
        Assert.assertFalse(err.isSuccess());
        Assert.assertEquals(expectedSqlState, err.getSqlState());
        Assert.assertEquals(expectedSqlCode, err.getSqlCode());
        l.debug(err.getMessage());
    }
    
    private void testSuccessResponse(JongoSuccess r, Response.Status expectedStatus, int expectedResults){
        List<Row> rows = r.getRows();
        Assert.assertEquals(expectedStatus, r.getStatus());
        Assert.assertTrue(r.isSuccess());
        Assert.assertEquals(expectedResults, rows.size());
    }
    
    private void testDynamicFinder(String resource, String finder, int expectedResults, String...  arr){
        JongoSuccess r = (JongoSuccess)controller.findByDynamicFinder(resource,finder, Arrays.asList(arr), limit, order);
        testSuccessResponse(r, Response.Status.OK, expectedResults);
    }
    
    private String getId(final JongoSuccess response, final String id){
        for(Row row : response.getRows()){
            for(String k : row.getCells().keySet()){
                if(k.equalsIgnoreCase(id)){
                    return row.getCells().get(k);
                }
            }
        }
        return null;
    }
}
