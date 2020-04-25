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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import amforeas.AmforeasUtils;
import amforeas.config.AmforeasConfiguration;
import amforeas.demo.Demo;
import amforeas.enums.Operator;
import amforeas.exceptions.AmforeasBadRequestException;
import amforeas.exceptions.StartupException;
import amforeas.jdbc.JDBCExecutor;
import amforeas.jdbc.StoredProcedureParam;
import amforeas.rest.xstream.Row;
import amforeas.sql.*;

import org.amforeas.mocks.UserMock;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Alejandro Ayuso
 */
public class JDBCExecutorTest {

    @BeforeClass
    public static void setUp () throws StartupException {
        System.setProperty("environment", "demo");
        AmforeasUtils.loadConfiguration();
    }

    @AfterClass
    public static void tearDownClass () throws Exception {
        System.setProperty("environment", "demo");
        AmforeasConfiguration configuration = AmforeasUtils.loadConfiguration();
        Demo.destroyDemoDatabases(configuration.getDatabases());
        AmforeasConfiguration.reset();
    }

    @Test
    public void testGet () throws SQLException {
        Table t = new Table("my_demo_db", "users");

        // select * from users
        List<Row> rs = JDBCExecutor.get(new Select(t), true);
        assertEquals(2, rs.size());

        // select * from car
        rs = JDBCExecutor.get(new Select(new Table("my_demo_db", "car", "cid")), true);
        assertEquals(3, rs.size());

        // select * from car where cid = 0
        rs = JDBCExecutor.get(
                new Select(new Table("my_demo_db", "car", "cid")).setParameter(new SelectParam("cid", "0")), false);
        assertEquals(1, rs.size());

        // select * from users where id = 0
        rs = JDBCExecutor.get(new Select(t).setParameter(new SelectParam(t.getPrimaryKey(), "0")), false);
        assertEquals(1, rs.size());

        // select * from users where name = bar
        rs = JDBCExecutor.get(new Select(t).setParameter(new SelectParam("name", "bar")), true);
        assertEquals(1, rs.size());

        // select birthday from users where name = bar
        rs = JDBCExecutor.get(new Select(t).setParameter(new SelectParam("name", "bar")).addColumn("birthday"), true);
        assertEquals(1, rs.size());
        assertFalse(rs.get(0).getCells().containsKey("credit"));
        assertTrue(rs.get(0).getCells().containsKey("birthday"));

        // select birthday from users where id = 1
        rs = JDBCExecutor.get(new Select(t).addColumn("birthday").setParameter(new SelectParam("id", "1")), true);
        assertEquals(1, rs.size());
        assertFalse(rs.get(0).getCells().containsKey("credit"));
        assertTrue(rs.get(0).getCells().containsKey("birthday"));

        // select birthday from users
        rs = JDBCExecutor.get(new Select(t).addColumn("birthday"), true);
        assertEquals(2, rs.size());
        assertFalse(rs.get(0).getCells().containsKey("credit"));
        assertTrue(rs.get(0).getCells().containsKey("birthday"));
    }

    @Test
    public void testAll () throws SQLException {
        Table t = new Table("my_demo_db", "users");
        List<UserMock> users = getTestValues();
        List<UserMock> createdusers = new ArrayList<UserMock>();
        for (UserMock u : users) {
            Insert i = new Insert(t).setColumns(u.toMap());
            int r = JDBCExecutor.insert(i);
            assertEquals(1, r);
            createdusers.add(u);

            Select s = new Select(t).setParameter(new SelectParam("name", Operator.EQUALS, u.name));

            List<Row> rs = JDBCExecutor.get(s, true);
            Row row = rs.get(0);
            assertEquals(String.valueOf(u.age), row.getCells().get("age"));

            Update up = new Update(t);
            up.addColumn("name", "foo1").setId(row.getCells().get("id"));

            rs = JDBCExecutor.update(up);
            row = rs.get(0);
            assertEquals(String.valueOf(u.age), row.getCells().get("age"));

            Delete d = new Delete(t).setId(row.getCells().get("id"));
            r = JDBCExecutor.delete(d);
            assertEquals(1, r);
        }
    }

    @Test
    public void testInsert () throws SQLException {
        Table t = new Table("my_demo_db", "users");
        try {
            JDBCExecutor.insert(new Insert(t));
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        Map<String, String> params = UserMock.getRandomInstance().toMap();
        assertEquals(1, JDBCExecutor.insert(new Insert(t).setColumns(params)));

        // test if one of the params is null
        params.put("age", null);
        assertEquals(1, JDBCExecutor.insert(new Insert(t).setColumns(params)));

        // test if one of the params is empty
        params.put("age", "30");
        params.put("name", "");
        assertEquals(1, JDBCExecutor.insert(new Insert(t).setColumns(params)));

        // clean up
        JDBCExecutor.delete(new Delete(t).setId("2"));
        JDBCExecutor.delete(new Delete(t).setId("3"));
        JDBCExecutor.delete(new Delete(t).setId("4"));

        // test with a readonly table
        try {
            JDBCExecutor.insert(new Insert(new Table("my_demo_db", "maker")).addColumn("name", "RO"));
        } catch (SQLException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testUpdate () throws SQLException {
        Table t = new Table("my_demo_db", "users");
        Select s = new Select(t).setParameter(new SelectParam(t.getPrimaryKey(), Operator.EQUALS, "0"));
        Row row = JDBCExecutor.get(s, false).get(0);
        assertEquals("0", row.getCells().get("id"));

        List<Row> rs = JDBCExecutor.update(new Update(t).setId("0").addColumn("age", "60"));
        row = rs.get(0);
        assertEquals("0", row.getCells().get("id"));
        assertEquals("foo", row.getCells().get("name"));
        assertEquals("60", row.getCells().get("age"));

        rs = JDBCExecutor.update(new Update(t).setId("0").addColumn("age", "70").addColumn("name", "foooer"));
        row = rs.get(0);
        assertEquals("0", row.getCells().get("id"));
        assertEquals("foooer", row.getCells().get("name"));
        assertEquals("70", row.getCells().get("age"));

        // test for empty value
        rs = JDBCExecutor.update(new Update(t).setId("0").addColumn("name", ""));
        row = rs.get(0);
        assertEquals("0", row.getCells().get("id"));
        assertEquals("", row.getCells().get("name"));

        // test for null value
        rs = JDBCExecutor.update(new Update(t).setId("0").addColumn("age", null));
        row = rs.get(0);
        assertEquals("0", row.getCells().get("id"));
        assertEquals(null, row.getCells().get("age"));
        assertTrue(row.getCells().containsKey("age"));
    }

    public List<UserMock> getTestValues () {
        List<UserMock> u1 = new ArrayList<UserMock>();
        u1.add(UserMock.getRandomInstance());
        u1.add(UserMock.getRandomInstance());
        u1.add(UserMock.getRandomInstance());
        return u1;
    }

    @Test
    public void testSimpleFunction () throws SQLException, AmforeasBadRequestException {
        List<StoredProcedureParam> params = new ArrayList<StoredProcedureParam>();
        List<Row> rows = JDBCExecutor.executeQuery("my_demo_db", "simpleStoredProcedure", params);
        assertEquals(1, rows.size());
    }

    @Test
    public void testSimpleStoredProcedure () throws AmforeasBadRequestException, SQLException {
        List<StoredProcedureParam> params = new ArrayList<StoredProcedureParam>();
        Select s = new Select(new Table("my_demo_db", "comments"));
        List<Row> rs = JDBCExecutor.get(s, true);
        assertEquals(3, rs.size());

        params.add(new StoredProcedureParam("car_id", "1", false, 1, "INTEGER"));
        params.add(new StoredProcedureParam("comment", "grrrr asdsa  asd asd asda asdd )/(&&/($%/(&$=)/&/$·/(&", false,
                2, "VARCHAR"));
        List<Row> rows = JDBCExecutor.executeQuery("my_demo_db", "insert_comment", params);
        assertEquals(0, rows.size());

        rs = JDBCExecutor.get(s, true);
        assertEquals(4, rs.size());
    }

    @Test
    public void testComplexStoredProcedure () throws AmforeasBadRequestException, SQLException {
        List<StoredProcedureParam> params;
        params = new ArrayList<StoredProcedureParam>();
        params.add(new StoredProcedureParam("in_year", "2010", false, 1, "INTEGER"));
        params.add(new StoredProcedureParam("out_total", "", true, 2, "INTEGER"));
        List<Row> rows = JDBCExecutor.executeQuery("my_demo_db", "get_year_sales", params);
        assertEquals(1, rows.size());
        assertEquals("12", rows.get(0).getCells().get("out_total"));
    }

    @Test
    public void testGetMetaData () throws AmforeasBadRequestException, SQLException {
        Table t = new Table("my_demo_db", "users");
        Select s = new Select(t);
        List<Row> rs = JDBCExecutor.getTableMetaData(s);
        assertEquals(6, rs.size());

        rs = JDBCExecutor.getListOfTables("my_demo_db");
        assertEquals(9, rs.size());

    }
}
