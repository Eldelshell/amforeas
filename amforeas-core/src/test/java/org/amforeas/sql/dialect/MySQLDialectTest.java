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
package org.amforeas.sql.dialect;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;
import amforeas.sql.Select;
import amforeas.sql.dialect.MySQLDialect;

@Tag("dialect-tests")
public class MySQLDialectTest extends SQLDialectTest {

    public MySQLDialectTest() {
        d = new MySQLDialect();
    }

    @Test
    @Override
    public void testDelete () {
        // TODO Implement
    }

    @Test
    @Override
    public void testInsert () {
        // TODO Implement
    }

    @Test
    @Override
    public void testSelect () {
        String sql = "SELECT t.* FROM demo1.a_table t";
        doTest(sql, new Select(table));

        sql = "SELECT t.name FROM demo1.a_table t";
        doTest(sql, new Select(table).addColumn("name"));

        sql = "SELECT t.name,t.age FROM demo1.a_table t";
        doTest(sql, new Select(table).addColumn("name").addColumn("age"));

        sql = "SELECT t.* FROM demo1.a_table t ORDER BY t.id ASC";
        doTest(sql, new Select(table).setOrderParam(new OrderParam("id", "ASC")));

        sql = "SELECT t.name,t.age FROM demo1.a_table t ORDER BY t.id ASC";
        doTest(sql, new Select(table).addColumn("name").addColumn("age").setOrderParam(new OrderParam("id", "ASC")));

        sql = "SELECT t.* FROM demo1.a_table t LIMIT 0,25";
        doTest(sql, new Select(table).setLimitParam(new LimitParam(25, 0)));

        sql = "SELECT t.name,t.age FROM demo1.a_table t ORDER BY t.name DESC LIMIT 0,25";
        doTest(sql, new Select(table).addColumn("name").addColumn("age").setLimitParam(new LimitParam(25, 0))
                .setOrderParam(new OrderParam("name", "DESC")));
    }

    @Test
    @Override
    public void testUpdate () {
        // TODO Implement
    }
}
