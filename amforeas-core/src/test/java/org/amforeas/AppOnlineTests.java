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
import java.util.List;
import javax.ws.rs.core.Response;

import amforeas.rest.xstream.AmforeasError;
import amforeas.rest.xstream.AmforeasResponse;
import amforeas.rest.xstream.AmforeasSuccess;
import amforeas.rest.xstream.Row;

import org.apache.http.NameValuePair;
import org.junit.jupiter.api.Tag;
import org.amforeas.mocks.AmforeasClient;
import org.amforeas.mocks.UserMock;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This tests are based on data generated when running in demo mode with the Demo.java class.
 */
@Tag("online-tests")
public class AppOnlineTests {

    private static final AmforeasClient client = new AmforeasClient();

    public static void main (String[] args) {
        AppOnlineTests app = new AppOnlineTests();
        app.testJongo();
        app.testErrors();
        app.testDynamicFinders();
        app.testPaging();
        app.testOrdering();
        app.testSQLInject();
    }

    public void testJongo () {
        List<UserMock> users = getTestValues();
        List<UserMock> createdusers = new ArrayList<UserMock>();
        for (UserMock user : users) {
            doTestResponse(client.doPOST("users", user.toNameValuePair()), Response.Status.CREATED, 1);
            createdusers.addAll(doTestResponse(client.doGET("users/name/" + user.name), Response.Status.OK, 1));
        }

        assertEquals(createdusers.size(), 3);

        for (UserMock user : createdusers) {
            // generate a new user to update all the values on an existing user
            UserMock newMock = UserMock.getRandomInstance();
            doTestResponse(client.doPUT("users/" + user.id, newMock.toJSON()), Response.Status.OK, 1);
            UserMock comingUser = doTestResponse(client.doGET("user/" + user.id), Response.Status.OK, 1).get(0);
            assertEquals(newMock.name, comingUser.name);
            // now delete them
            doTestResponse(client.doDELETE("users/" + user.id), Response.Status.OK, 1);
        }
    }

    public void testErrors () {
        doTestResponse(client.doGET("user/999"), Response.Status.NOT_FOUND, 0);
        // let's try an update/insert with invalid data
        doTestResponse(client.doPUT("comments/0", "{\"car_comment\":\"this should fail!\""),
                Response.Status.BAD_REQUEST, 0);
        doTestResponse(client.doPUT("pictures/0", "{}"), Response.Status.BAD_REQUEST, 0);
        doTestResponse(client.doPUT("pictures/0", ""), Response.Status.BAD_REQUEST, 0);
        doTestResponse(client.doPOST("pictures", "{}"), Response.Status.BAD_REQUEST, 0);
        doTestResponse(client.doPOST("pictures", ""), Response.Status.BAD_REQUEST, 0);
        doTestResponse(client.doPOST("pictures", new ArrayList<NameValuePair>()), Response.Status.BAD_REQUEST, 0);
        // in the demo, by default, maker is not writtable
        doTestResponse(client.doPOST("maker", "{\"maker\":\"this should fail!\",\"id\":1}"),
                Response.Status.BAD_REQUEST, 0);
        doTestResponse(client.doPUT("maker/0", "{\"maker\":\"this should fail!\"}"), Response.Status.BAD_REQUEST, 0);
        // table is not in Amforeas
        doTestResponse(client.doPOST("notInJongo", "{\"comment\":\"this should fail!\",\"cid\":1}"),
                Response.Status.BAD_REQUEST, 0);
        doTestResponse(client.doPUT("notInJongo/0", "{\"comment\":\"this should fail!\"}"), Response.Status.BAD_REQUEST,
                0);
    }

    public void testDynamicFinders () {
        doTestResponse(client.doGET("users/dynamic/findAllByAgeBetween?args=18&args=99"), Response.Status.OK, 2);
        doTestResponse(client.doGET("users/dynamic/findAllByBirthdayBetween?args=1992-01-01&args=1992-12-31"),
                Response.Status.OK, 1);
        doTestResponse(client.doGET("car/dynamic/findAllByFuelIsNull?sort=cid"), Response.Status.OK, 1);
        doTestResponse(client.doGET("car/dynamic/findAllByFuelIsNotNull?sort=cid"), Response.Status.OK, 2);
        doTestResponse(client.doGET("users/dynamic/findAllByCreditGreaterThan?args=0"), Response.Status.OK, 1);
        doTestResponse(client.doGET("users/dynamic/findAllByCreditGreaterThanEquals?args=0"), Response.Status.OK, 2);
        doTestResponse(client.doGET("users/dynamic/findAllByCreditLessThan?args=0"), Response.Status.NOT_FOUND, 0);
        doTestResponse(client.doGET("users/dynamic/findAllByCreditLessThanEquals?args=0"), Response.Status.OK, 1);
        doTestResponse(client.doGET(
                "sales_stats/dynamic/findAllByLast_updateBetween?args=2000-01-01T00:00:00.000Z&args=2000-06-01T23:55:00.000Z"),
                Response.Status.OK, 6);
    }

    public void testPaging () {
        doTestPagingResponse(client.doGET("maker_stats"), Response.Status.OK, 25, "id", "0", "24");
        doTestPagingResponse(client.doGET("maker_stats?limit=notAllowed"), Response.Status.OK, 25, "id", "0", "24");
        doTestPagingResponse(client.doGET("maker_stats?offset=50"), Response.Status.OK, 25, "id", "0", "24");
        doTestPagingResponse(client.doGET("maker_stats?limit=50"), Response.Status.OK, 50, "id", "0", "49");
        doTestPagingResponse(client.doGET("maker_stats?limit=50&offset=50"), Response.Status.OK, 50, "id", "50", "99");
        doTestResponse(client.doGET("maker_stats?limit=50&offset=15550"), Response.Status.OK, 0);
    }

    public void testOrdering () {
        doTestPagingResponse(client.doGET("car?idField=cid&sort=cid"), Response.Status.OK, 3, "model", "C2", "X5");
        doTestPagingResponse(client.doGET("car?sort=year"), Response.Status.OK, 3, "model", "C2", "X5");
        doTestPagingResponse(client.doGET("car?sort=year&dir=ASC"), Response.Status.OK, 3, "model", "C2", "X5");
        doTestPagingResponse(client.doGET("car?sort=year&dir=DESC"), Response.Status.OK, 3, "model", "X5", "C2");
        doTestPagingResponse(client.doGET("car?sort=maker&dir=ASC"), Response.Status.OK, 3, "maker", "BMW", "FIAT");
        doTestPagingResponse(client.doGET("car?sort=maker&dir=ASC"), Response.Status.OK, 3, "model", "X5", "500");
        doTestPagingResponse(client.doGET("car?sort=maker&dir=DESC"), Response.Status.OK, 3, "maker", "FIAT", "BMW");
        doTestPagingResponse(client.doGET("car?sort=maker&dir=DESC"), Response.Status.OK, 3, "model", "500", "X5");
        doTestPagingResponse(client.doGET("car?sort=maker&dir=ASC&limit=1"), Response.Status.OK, 1, "model", "X5",
                "X5");
        doTestPagingResponse(client.doGET("car?sort=maker&dir=DESC&limit=2"), Response.Status.OK, 2, "model", "500",
                "C2");
        doTestPagingResponse(client.doGET("car?sort=maker&dir=DESC&limit=2&offset=1"), Response.Status.OK, 2, "model",
                "C2", "X5");
    }

    public void testSQLInject () {
        doTestResponse(client.doPUT("users/0", "{\"name\":\"anything' OR 'x'='x'\"}"), Response.Status.OK, 1);
        doTestResponse(client.doGET("users/name/bar%20AND%20age%3D30"), Response.Status.NOT_FOUND, 0);
        doTestResponse(client.doGET("users/name/bar%3B%20DROP%20TABLE%20user%3B%20--"), Response.Status.NOT_FOUND, 0);
    }

    public List<UserMock> getTestValues () {
        List<UserMock> u1 = new ArrayList<UserMock>();
        u1.add(UserMock.getRandomInstance());
        u1.add(UserMock.getRandomInstance());
        u1.add(UserMock.getRandomInstance());
        return u1;

    }

    private List<UserMock> doTestResponse (AmforeasResponse r, Response.Status expectedStatus, int expectedCount) {
        List<UserMock> users = new ArrayList<UserMock>();
        assertNotNull(r);
        if (r instanceof AmforeasSuccess) {
            AmforeasSuccess s = (AmforeasSuccess) r;
            List<Row> rows = s.getRows();
            assertEquals(rows.size(), expectedCount);
            for (Row row : rows) {
                users.add(UserMock.instanceOf(row.getCells()));
            }
        } else {
            AmforeasError e = (AmforeasError) r;
        }
        return users;
    }

    private void doTestPagingResponse (AmforeasResponse r, Response.Status expectedStatus, int expectedCount,
            String col, String first, String last) {
        assertNotNull(r);
        if (r instanceof AmforeasSuccess) {
            AmforeasSuccess s = (AmforeasSuccess) r;
            List<Row> rows = s.getRows();
            int lastIndex = s.getRows().size() - 1;
            assertEquals(rows.size(), expectedCount);
            assertEquals(first, rows.get(0).getCells().get(col));
            assertEquals(last, rows.get(lastIndex).getCells().get(col));
        } else {
            AmforeasError e = (AmforeasError) r;
            assertFalse(e.isSuccess());
        }
    }
}
