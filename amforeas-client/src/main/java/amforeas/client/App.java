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

package amforeas.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import amforeas.jdbc.StoredProcedureParam;
import amforeas.rest.xstream.AmforeasResponse;
import amforeas.rest.xstream.SuccessResponse;

public class App {

    private static final Logger l = LoggerFactory.getLogger(App.class);

    private final static String table = "users";

    public static void main (String[] args) {

        l.info("Running Amforeas Application on http://localhost:8080/amforeas/demo1");

        AmforeasClient demo = new AmforeasClient("http", "localhost", 8080, "amforeas", "demo1");
        demo.meta();
        demo.meta(table);

        demo.getAll(table);
        demo.get(RequestParams.builder(table).page(1).sortBy("name", "desc").build());

        demo.get(table, "1");
        demo.get(RequestParams.builder(table).id("1").build());

        // Optional<AmforeasResponse> r1 = demo.find(table, "name", "waka waka");
        Optional<AmforeasResponse> r1 = demo.get(RequestParams.builder(table).column("name").value("waka waka").build());

        if (r1.isPresent() && r1.get().isSuccess()) {
            SuccessResponse sr = (SuccessResponse) r1.get();
            String id = sr.getRows().get(0).getCells().get("id");
            // demo.update(table, id, getUserAsJSON("waka waka"));
            r1 = demo.put(RequestParams.builder(table).id(id).update(getUserAsForm("waka waka")).build(), MediaType.APPLICATION_JSON);
            assertIsSuccess(r1, "Should update");
        } else {
            // demo.add(table, getUserAsForm("waka waka"));
            r1 = demo.post(RequestParams.builder(table).insert(getUserAsForm("waka waka")).build(), MediaType.APPLICATION_JSON);
            assertIsSuccess(r1, "Should insert");
        }

        r1 = demo.find(table, "name", "hehe");
        assertIsError(r1, "Shouldn't find this user");

        String name = RandomStringUtils.random(10, "abcdefghijklmn");
        demo.add(table, getUserAsJSON(name));

        r1 = demo.find(table, "name", name);
        assertIsSuccess(r1, "Shouldn find this user");

        SuccessResponse sr = (SuccessResponse) r1.get();
        String id = sr.getRows().get(0).getCells().get("id");
        // demo.delete(table, id);
        r1 = demo.delete(RequestParams.builder(table).id(id).build());
        assertIsSuccess(r1, "Should delete");

        r1 = demo.find(table, "name", name);
        assertIsError(r1, "Shouldn't find this user");

        // Dynamic Query
        r1 = demo.query(table, "findByAgeEquals", "30");
        assertIsSuccess(r1, "Should find this user");

        r1 = demo.get(RequestParams.builder(table).dynamicQuery("findByAgeEquals").addQueryParam("30").build());
        assertIsSuccess(r1, "Should find this user");

        r1 = demo.query(table, "findAllByAgeBetween", "30", "40");
        assertIsSuccess(r1, "Should find this user");

        // SELECT * FROM users WHERE age BETWEEN ? AND ? ORDER BY age DESC LIMIT 1 OFFSET 0
        r1 = demo.get(RequestParams.builder(table).dynamicQuery("findAllByAgeBetween").addQueryParam("30").addQueryParam("40").from(0).to(1).sortBy("age", "desc").build());
        assertIsSuccess(r1, "Should find this user");

        /* SPs */
        r1 = demo.call("simpleStoredProcedure");
        assertIsSuccess(r1, "Should return 1");

        r1 = demo.call("insert_comment",
            new StoredProcedureParam("car_id", "1", false, 1, "INTEGER"),
            new StoredProcedureParam("car_comment", "JUst a comment from Java Client", false, 2, "VARCHAR"));

        assertIsSuccess(r1, "Should work");

        demo.getAll("comments");

        r1 = demo.call("get_year_sales",
            new StoredProcedureParam("in_year", "2001", false, 1, "INTEGER"),
            new StoredProcedureParam("out_total", null, true, 2, "INTEGER"));

        assertIsSuccess(r1, "Should be 12");
    }

    private static List<NameValuePair> getUserAsForm (String name) {
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("name", name));
        data.add(new BasicNameValuePair("age", RandomUtils.nextInt(30, 90) + ""));
        data.add(new BasicNameValuePair("birthday", "1900-12-15"));
        data.add(new BasicNameValuePair("credit", "999.95"));
        return data;
    }

    private static String getUserAsJSON (String name) {
        String birthday = "1900-12-15", credit = "9999.97";
        int age = RandomUtils.nextInt(30, 90);
        String json = "{\"name\":\"%s\",\"birthday\":\"%s\",\"credit\":\"%s\",\"age\":\"%s\"}";
        return String.format(json, name, birthday, credit, age);
    }

    private static void assertIsSuccess (Optional<AmforeasResponse> r1, String fail) {
        if (r1.isPresent() && !r1.get().isSuccess()) {
            throw new IllegalStateException(fail);
        }
    }

    private static void assertIsError (Optional<AmforeasResponse> r1, String fail) {
        if (r1.isPresent() && r1.get().isSuccess()) {
            throw new IllegalStateException(fail);
        }
    }

    /* Do some of this tests here */

    // public void testApp () {
    // List<UserMock> users = getTestValues();
    // List<UserMock> createdusers = new ArrayList<UserMock>();
    // for (UserMock user : users) {
    // doTestResponse(client.doPOST("users", user.toNameValuePair()), Response.Status.CREATED, 1);
    // createdusers.addAll(doTestResponse(client.doGET("users/name/" + user.name), Response.Status.OK, 1));
    // }
    //
    // assertEquals(createdusers.size(), 3);
    //
    // for (UserMock user : createdusers) {
    // // generate a new user to update all the values on an existing user
    // UserMock newMock = UserMock.getRandomInstance();
    // doTestResponse(client.doPUT("users/" + user.id, newMock.toJSON()), Response.Status.OK, 1);
    // UserMock comingUser = doTestResponse(client.doGET("users/" + user.id), Response.Status.OK, 1).get(0);
    // assertEquals(newMock.name, comingUser.name);
    // // now delete them
    // doTestResponse(client.doDELETE("users/" + user.id), Response.Status.OK, 1);
    // }
    // }
    //
    // public void testErrors () {
    // doTestResponse(client.doGET("user/999"), Response.Status.NOT_FOUND, 0);
    // // let's try an update/insert with invalid data
    // doTestResponse(client.doPUT("comments/0", "{\"car_comment\":\"this should fail!\""), Response.Status.BAD_REQUEST, 0);
    // doTestResponse(client.doPUT("pictures/0", "{}"), Response.Status.BAD_REQUEST, 0);
    // doTestResponse(client.doPUT("pictures/0", ""), Response.Status.BAD_REQUEST, 0);
    // doTestResponse(client.doPOST("pictures", "{}"), Response.Status.BAD_REQUEST, 0);
    // doTestResponse(client.doPOST("pictures", ""), Response.Status.BAD_REQUEST, 0);
    // doTestResponse(client.doPOST("pictures", new ArrayList<NameValuePair>()), Response.Status.BAD_REQUEST, 0);
    // // in the demo, by default, maker is not writtable
    // doTestResponse(client.doPOST("maker", "{\"maker\":\"this should fail!\",\"id\":1}"),
    // Response.Status.BAD_REQUEST, 0);
    // doTestResponse(client.doPUT("maker/0", "{\"maker\":\"this should fail!\"}"), Response.Status.BAD_REQUEST, 0);
    // // table is not in Amforeas
    // doTestResponse(client.doPOST("notInApp", "{\"comment\":\"this should fail!\",\"cid\":1}"), Response.Status.BAD_REQUEST, 0);
    // doTestResponse(client.doPUT("notInApp/0", "{\"comment\":\"this should fail!\"}"), Response.Status.BAD_REQUEST, 0);
    // }
    //
    // public void testDynamicFinders () {
    // doTestResponse(client.doGET("users/dynamic/findAllByAgeBetween?args=18&args=99"), Response.Status.OK);
    // doTestResponse(client.doGET("users/dynamic/findAllByBirthdayBetween?args=1992-01-01&args=1992-12-31"), Response.Status.OK);
    // doTestResponse(client.doGET("car/dynamic/findAllByFuelIsNull?sort=cid"), Response.Status.OK, 1);
    // doTestResponse(client.doGET("car/dynamic/findAllByFuelIsNotNull?sort=cid"), Response.Status.OK, 2);
    // doTestResponse(client.doGET("users/dynamic/findAllByCreditGreaterThan?args=0"), Response.Status.OK);
    // doTestResponse(client.doGET("users/dynamic/findAllByCreditGreaterThanEquals?args=0"), Response.Status.OK);
    // doTestResponse(client.doGET("users/dynamic/findAllByCreditLessThan?args=0"), Response.Status.NOT_FOUND);
    // doTestResponse(client.doGET("users/dynamic/findAllByCreditLessThanEquals?args=0"), Response.Status.OK);
    // doTestResponse(client.doGET(
    // "sales_stats/dynamic/findAllByLast_updateBetween?args=2000-01-01T00:00:00.000Z&args=2000-06-01T23:55:00.000Z"),
    // Response.Status.OK, 6);
    // }
    //
    // public void testPaging () {
    // doTestPagingResponse(client.doGET("maker_stats"), Response.Status.OK, 25, "id", "0", "24");
    // doTestPagingResponse(client.doGET("maker_stats?limit=notAllowed"), Response.Status.OK, 25, "id", "0", "24");
    // doTestPagingResponse(client.doGET("maker_stats?offset=50"), Response.Status.OK, 25, "id", "0", "24");
    // doTestPagingResponse(client.doGET("maker_stats?limit=50"), Response.Status.OK, 50, "id", "0", "49");
    // doTestPagingResponse(client.doGET("maker_stats?limit=50&offset=50"), Response.Status.OK, 50, "id", "50", "99");
    // doTestResponse(client.doGET("maker_stats?limit=50&offset=15550"), Response.Status.OK, 0);
    // }
    //
    // public void testOrdering () {
    // doTestPagingResponse(client.doGET("car?idField=cid&sort=cid"), Response.Status.OK, 3, "model", "C2", "X5");
    // doTestPagingResponse(client.doGET("car?sort=year"), Response.Status.OK, 3, "model", "C2", "X5");
    // doTestPagingResponse(client.doGET("car?sort=year&dir=ASC"), Response.Status.OK, 3, "model", "C2", "X5");
    // doTestPagingResponse(client.doGET("car?sort=year&dir=DESC"), Response.Status.OK, 3, "model", "X5", "C2");
    // doTestPagingResponse(client.doGET("car?sort=maker&dir=ASC"), Response.Status.OK, 3, "maker", "BMW", "FIAT");
    // doTestPagingResponse(client.doGET("car?sort=maker&dir=ASC"), Response.Status.OK, 3, "model", "X5", "500");
    // doTestPagingResponse(client.doGET("car?sort=maker&dir=DESC"), Response.Status.OK, 3, "maker", "FIAT", "BMW");
    // doTestPagingResponse(client.doGET("car?sort=maker&dir=DESC"), Response.Status.OK, 3, "model", "500", "X5");
    // doTestPagingResponse(client.doGET("car?sort=maker&dir=ASC&limit=1"), Response.Status.OK, 1, "model", "X5", "X5");
    // doTestPagingResponse(client.doGET("car?sort=maker&dir=DESC&limit=2"), Response.Status.OK, 2, "model", "500", "C2");
    // doTestPagingResponse(client.doGET("car?sort=maker&dir=DESC&limit=2&offset=1"), Response.Status.OK, 2, "model", "C2", "X5");
    // }
    //
    // public void testSQLInject () {
    // doTestResponse(client.doPUT("users/0", "{\"name\":\"anything' OR 'x'='x'\"}"), Response.Status.OK);
    // doTestResponse(client.doGET("users/name/bar%20AND%20age%3D30"), Response.Status.NOT_FOUND, 0);
    // doTestResponse(client.doGET("users/name/bar%3B%20DROP%20TABLE%20user%3B%20--"), Response.Status.NOT_FOUND, 0);
    // }

}
