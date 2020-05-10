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

package org.amforeas.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import amforeas.SingletonFactory;
import amforeas.SingletonFactoryImpl;
import amforeas.demo.Demo;
import amforeas.demo.DemoSingletonFactory;
import amforeas.enums.Operator;
import amforeas.exceptions.AmforeasBadRequestException;
import amforeas.exceptions.StartupException;
import amforeas.jdbc.JDBCExecutor;
import amforeas.jdbc.OrderParam;
import amforeas.jdbc.StoredProcedureParam;
import amforeas.rest.xstream.Row;
import amforeas.sql.Delete;
import amforeas.sql.Insert;
import amforeas.sql.Select;
import amforeas.sql.SelectParam;
import amforeas.sql.Table;
import amforeas.sql.Update;

/**
 * Tests of {@link amforeas.jdbc.executor}
 */
@ExtendWith(MockitoExtension.class)
@Tag("sql-tests")
public class JDBCExecutorTest {

    private JDBCExecutor executor;

    @BeforeEach
    public void setUpEach () {
        SingletonFactory factory = new DemoSingletonFactory();
        executor = factory.getJDBCExecutor();
    }

    @BeforeAll
    public static void setUp () throws StartupException {
        SingletonFactory factory = new DemoSingletonFactory();
        factory.getConfiguration();
    }

    @AfterAll
    public static void tearDownClass () throws Exception {
        SingletonFactory factory = new SingletonFactoryImpl();
        Demo.destroyDemoDatabases(factory.getConfiguration().getDatabases());
        factory.resetConfiguration();
    }

    @Test
    public void testGet () throws SQLException {
        Table t = new Table("my_demo_db", "users");

        // select * from users
        List<Row> rs = executor.get(new Select(t), true);
        assertEquals(6, rs.size());

        // select * from car
        rs = executor.get(new Select(new Table("my_demo_db", "car", "cid")), true);
        assertEquals(3, rs.size());

        // select * from car where cid = 0
        rs = executor.get(new Select(new Table("my_demo_db", "car", "cid")).setParameter(new SelectParam("cid", "0")), false);
        assertEquals(1, rs.size());

        // select * from users where id = 0
        rs = executor.get(new Select(t).setParameter(new SelectParam(t.getPrimaryKey(), "0")), false);
        assertEquals(1, rs.size());

        // select * from users where name = bar
        rs = executor.get(new Select(t).setParameter(new SelectParam("name", "bar")), true);
        assertEquals(1, rs.size());

        // select birthday from users where name = bar
        rs = executor.get(new Select(t).setParameter(new SelectParam("name", "bar")).addColumn("birthday"), true);
        assertEquals(1, rs.size());
        assertFalse(rs.get(0).getCells().containsKey("credit"));
        assertTrue(rs.get(0).getCells().containsKey("birthday"));

        // select birthday from users where id = 1
        rs = executor.get(new Select(t).addColumn("birthday").setParameter(new SelectParam("id", "1")), true);
        assertEquals(1, rs.size());
        assertFalse(rs.get(0).getCells().containsKey("credit"));
        assertTrue(rs.get(0).getCells().containsKey("birthday"));

        // select birthday from users
        rs = executor.get(new Select(t).addColumn("birthday"), true);
        assertEquals(6, rs.size());
        assertFalse(rs.get(0).getCells().containsKey("credit"));
        assertTrue(rs.get(0).getCells().containsKey("birthday"));

        // select birthday from users and order by age
        rs = executor.get(new Select(t).addColumn("birthday").setOrderParam(new OrderParam("age", "desc")), true);
        assertEquals(6, rs.size());
        assertFalse(rs.get(0).getCells().containsKey("age"));
        assertTrue(rs.get(0).getCells().containsKey("birthday"));
    }

    @Test
    public void testGet_Between () throws SQLException {
        Table t = new Table("my_demo_db", "users");
        Select s = new Select(t);
        s.setParameter(new SelectParam("id", Operator.BETWEEN, "1", "3"));
        List<Row> rs = executor.get(s, true);
        assertEquals(3, rs.size());
    }

    @Test
    public void testGet_Like () throws SQLException {
        Table t = new Table("my_demo_db", "users");
        Select s = new Select(t);
        s.setParameter(new SelectParam("name", Operator.LIKE, "bar%"));
        List<Row> rs = executor.get(s, true);
        assertEquals(5, rs.size());
    }

    @Test
    public void testAll () throws SQLException {
        Table t = new Table("my_demo_db", "users");
        List<UserMock> users = getTestValues();
        List<UserMock> createdusers = new ArrayList<UserMock>();
        for (UserMock u : users) {
            Insert i = new Insert(t).setColumns(u.toMap());
            int r = executor.insert(i);
            assertEquals(1, r);
            createdusers.add(u);

            Select s = new Select(t).setParameter(new SelectParam("name", Operator.EQUALS, u.name));

            List<Row> rs = executor.get(s, true);
            Row row = rs.get(0);
            assertEquals(u.age, (int) row.getCells().get("age"));

            Update up = new Update(t);
            up.addColumn("name", "foo1").setId(row.getCells().get("id").toString());

            rs = executor.update(up);
            row = rs.get(0);
            assertEquals(u.age, (int) row.getCells().get("age"));

            Delete d = new Delete(t).setId(row.getCells().get("id").toString());
            r = executor.delete(d);
            assertEquals(1, r);
        }
    }

    @Test
    public void testInsert () throws SQLException {
        Table t = new Table("my_demo_db", "users");
        try {
            executor.insert(new Insert(t));
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        Map<String, String> params = UserMock.getRandomInstance().toMap();
        assertEquals(1, executor.insert(new Insert(t).setColumns(params)));

        // test if one of the params is null
        params.put("age", null);
        assertEquals(1, executor.insert(new Insert(t).setColumns(params)));

        // test if one of the params is empty
        params.put("age", "30");
        params.put("name", "");
        assertEquals(1, executor.insert(new Insert(t).setColumns(params)));

        // clean up
        executor.delete(new Delete(t).setId("2"));
        executor.delete(new Delete(t).setId("3"));
        executor.delete(new Delete(t).setId("4"));

        // test with a readonly table
        try {
            executor.insert(new Insert(new Table("my_demo_db", "maker")).addColumn("name", "RO"));
        } catch (SQLException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testUpdate () throws SQLException {
        Table t = new Table("my_demo_db", "users");
        Select s = new Select(t).setParameter(new SelectParam(t.getPrimaryKey(), Operator.EQUALS, "0"));
        Row row = executor.get(s, false).get(0);
        assertEquals(0, (int) row.getCells().get("id"));
        assertTrue(row.getCells().containsKey("age"));
        assertFalse(row.getCells().containsKey("photo"));

        List<Row> rs = executor.update(new Update(t).setId("0").addColumn("age", "60"));
        row = rs.get(0);
        assertEquals(0, (int) row.getCells().get("id"));
        assertEquals("foo", row.getCells().get("name"));
        assertEquals(60, (int) row.getCells().get("age"));
        assertFalse(row.getCells().containsKey("photo"));

        rs = executor.update(new Update(t).setId("0").addColumn("age", "70").addColumn("name", "foooer"));
        row = rs.get(0);
        assertEquals(0, (int) row.getCells().get("id"));
        assertEquals("foooer", row.getCells().get("name"));
        assertEquals(70, (int) row.getCells().get("age"));
        assertFalse(row.getCells().containsKey("photo"));

        // test for empty value
        rs = executor.update(new Update(t).setId("0").addColumn("name", ""));
        row = rs.get(0);
        assertEquals(0, (int) row.getCells().get("id"));
        assertEquals("", row.getCells().get("name"));
        assertFalse(row.getCells().containsKey("photo"));

        // test for null value
        rs = executor.update(new Update(t).setId("0").addColumn("age", null));
        row = rs.get(0);
        assertEquals(0, (int) row.getCells().get("id"));
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
        List<Row> rows = executor.executeQuery("my_demo_db", "simpleStoredProcedure", params);
        assertEquals(1, rows.size());
    }

    @Test
    public void testSimpleStoredProcedure () throws AmforeasBadRequestException, SQLException {
        List<StoredProcedureParam> params = new ArrayList<StoredProcedureParam>();
        Select s = new Select(new Table("my_demo_db", "comments"));
        List<Row> rs = executor.get(s, true);
        assertEquals(3, rs.size());

        params.add(new StoredProcedureParam("car_id", "1", false, 1, "INTEGER"));
        params.add(new StoredProcedureParam("comment", "grrrr asdsa  asd asd asda asdd )/(&&/($%/(&$=)/&/$·/(&", false,
            2, "VARCHAR"));
        List<Row> rows = executor.executeQuery("my_demo_db", "insert_comment", params);
        assertEquals(0, rows.size());

        rs = executor.get(s, true);
        assertEquals(4, rs.size());
    }

    @Test
    public void testComplexStoredProcedure () throws AmforeasBadRequestException, SQLException {
        List<StoredProcedureParam> params;
        params = new ArrayList<StoredProcedureParam>();
        params.add(new StoredProcedureParam("in_year", "2010", false, 1, "INTEGER"));
        params.add(new StoredProcedureParam("out_total", "", true, 2, "INTEGER"));
        List<Row> rows = executor.executeQuery("my_demo_db", "get_year_sales", params);
        assertEquals(1, rows.size());
        assertEquals("12", rows.get(0).getCells().get("out_total"));
    }

    @Test
    public void testGetMetaData () throws AmforeasBadRequestException, SQLException {
        Table t = new Table("my_demo_db", "users");
        Select s = new Select(t);
        List<Row> rs = executor.getTableMetaData(s);
        assertEquals(7, rs.size());

        rs = executor.getListOfTables("my_demo_db");
        assertEquals(10, rs.size());

    }

    @Test
    public void testCount () {
        Table t = new Table("my_demo_db", "car");
        assertEquals(3, executor.count(t));
    }
}
