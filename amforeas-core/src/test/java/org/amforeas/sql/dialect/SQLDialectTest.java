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

package org.amforeas.sql.dialect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.enums.Operator;
import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;
import amforeas.sql.Delete;
import amforeas.sql.Insert;
import amforeas.sql.Select;
import amforeas.sql.SelectParam;
import amforeas.sql.Table;
import amforeas.sql.Update;
import amforeas.sql.dialect.Dialect;
import amforeas.sql.dialect.SQLDialect;

/**
 *
 */
@Tag("dialect-tests")
public class SQLDialectTest {

    Table table = new Table("demo1", "a_table", "tableId");
    Dialect d;

    OrderParam o = new OrderParam(table);
    LimitParam l = new LimitParam();

    public SQLDialectTest() {
        d = new SQLDialect();
    }

    @Test
    public void testSelect () {
        doTest("SELECT t.* FROM demo1.a_table t", new Select(table));

        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId = ?",
            new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), Operator.EQUALS, "1")));

        doTest("SELECT t.age,t.name FROM demo1.a_table t WHERE t.tableId = ?",
            new Select(table).addColumn("age").addColumn("name").setParameter(new SelectParam(table.getPrimaryKey(), Operator.EQUALS, "1")));

        doTest("SELECT t.* FROM demo1.a_table t WHERE t.name = ?",
            new Select(table).setParameter(new SelectParam("name", Operator.EQUALS, "1")));

        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId = ? ORDER BY t.tableId ASC",
            new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), Operator.EQUALS, "1"))
                .setOrderParam(new OrderParam(table)));

        doTest("SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY t.tableId ) AS ROW_NUMBER, t.* FROM demo1.a_table t WHERE t.name = ?) WHERE ROW_NUMBER BETWEEN 0 AND 25",
            new Select(table).setParameter(new SelectParam("name", Operator.EQUALS, "1"))
                .setLimitParam(new LimitParam()));

        doTest("SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY t.tableId ) AS ROW_NUMBER, t.age,t.name FROM demo1.a_table t WHERE t.name = ?) WHERE ROW_NUMBER BETWEEN 0 AND 25",
            new Select(table).addColumn("age").addColumn("name").setParameter(new SelectParam("name", Operator.EQUALS, "1"))
                .setLimitParam(new LimitParam()));

        doTest("SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY t.name DESC ) AS ROW_NUMBER, t.* FROM demo1.a_table t WHERE t.tableId = ?) WHERE ROW_NUMBER BETWEEN 0 AND 25",
            new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), Operator.EQUALS, "1"))
                .setLimitParam(l).setOrderParam(new OrderParam("name", "DESC")));
    }

    @Test
    public void testSelect_between () {
        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId BETWEEN ? AND ?",
            new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), Operator.BETWEEN, "1", "2")));

        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId BETWEEN ? AND ? ORDER BY t.tableId ASC",
            new Select(table).setParameter(
                new SelectParam(table.getPrimaryKey(), Operator.BETWEEN, "1", "2")).setOrderParam(new OrderParam(table)));

        doTest("SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY t.tableId ) AS ROW_NUMBER, t.* FROM demo1.a_table t WHERE t.name BETWEEN ? AND ?) WHERE ROW_NUMBER BETWEEN 0 AND 25",
            new Select(table).setParameter(new SelectParam("name", Operator.BETWEEN, "1", "2"))
                .setLimitParam(new LimitParam()));
    }

    @Test
    public void testSelect_like () {
        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId LIKE ?",
            new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), Operator.LIKE, "1")));

        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId LIKE ? ORDER BY t.tableId ASC",
            new Select(table).setParameter(
                new SelectParam(table.getPrimaryKey(), Operator.LIKE, "1")).setOrderParam(new OrderParam(table)));

        doTest("SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY t.tableId ) AS ROW_NUMBER, t.* FROM demo1.a_table t WHERE t.name LIKE ?) WHERE ROW_NUMBER BETWEEN 0 AND 25",
            new Select(table).setParameter(new SelectParam("name", Operator.LIKE, "1"))
                .setLimitParam(new LimitParam()));
    }

    @Test
    public void testSelect_isNull () {
        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId IS NULL",
            new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), Operator.ISNULL)));

        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId IS NULL ORDER BY t.tableId ASC",
            new Select(table).setParameter(
                new SelectParam(table.getPrimaryKey(), Operator.ISNULL)).setOrderParam(new OrderParam(table)));

        doTest("SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY t.tableId ) AS ROW_NUMBER, t.* FROM demo1.a_table t WHERE t.name IS NULL) WHERE ROW_NUMBER BETWEEN 0 AND 25",
            new Select(table).setParameter(new SelectParam("name", Operator.ISNULL))
                .setLimitParam(new LimitParam()));
    }

    @Test
    public void testSelect_isNotNull () {
        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId IS NOT NULL",
            new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), Operator.ISNOTNULL)));

        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId IS NOT NULL ORDER BY t.tableId ASC",
            new Select(table).setParameter(
                new SelectParam(table.getPrimaryKey(), Operator.ISNOTNULL)).setOrderParam(new OrderParam(table)));

        doTest("SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY t.tableId ) AS ROW_NUMBER, t.* FROM demo1.a_table t WHERE t.name IS NOT NULL) WHERE ROW_NUMBER BETWEEN 0 AND 25",
            new Select(table).setParameter(new SelectParam("name", Operator.ISNOTNULL))
                .setLimitParam(new LimitParam()));
    }

    @Test
    public void testDelete () {
        doTest("DELETE FROM a_table WHERE tableId=?", new Delete(table).setId("1"));
        doTest("DELETE FROM grrr WHERE id=?", new Delete(new Table("demo1", "grrr")).setId("1"));
    }

    @Test
    public void testInsert () {
        assertThrows(IllegalArgumentException.class, () -> doTest("", new Insert(table)));
        doTest("INSERT INTO a_table (name) VALUES (?)", new Insert(table).addColumn("name", "foo bar"));
        doTest("INSERT INTO a_table (name,age) VALUES (?,?)", new Insert(table).addColumn("name", "foo bar").addColumn("age", "50"));
    }

    @Test
    public void testUpdate () {
        String sql = "UPDATE a_table SET name=? WHERE tableId=?";
        doTest(sql, new Update(table).setId("1").addColumn("name", "foo bar"));

        sql = "UPDATE a_table SET name=?,age=? WHERE tableId=?";
        doTest(sql, new Update(table).setId("1").addColumn("name", "foo bar").addColumn("age", "50"));

        sql = "UPDATE a_table SET name=?,age=?,sex=? WHERE tableId=?";
        doTest(sql, new Update(table).setId("1").addColumn("name", "foo bar").addColumn("age", "50").addColumn("sex", "male"));
    }

    @Test
    public void test_rowCountStatement () {
        String sql = "SELECT COUNT(*) AS total FROM a_table";
        assertEquals(sql, d.rowCountStatement(table));
    }

    @Test
    public void testSelect_columns () {
        doTest("SELECT t.a FROM demo1.a_table t", new Select(table).addColumn("a"));
        doTest("SELECT t.a,t.b FROM demo1.a_table t", new Select(table).addColumn("a").addColumn("b"));

        var sql = "SELECT t.a FROM demo1.a_table t WHERE t.tableId IS NULL";
        var sel = new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), Operator.ISNULL)).addColumn("a");
        doTest(sql, sel);

        sql = "SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY t.name DESC ) AS ROW_NUMBER, t.a FROM demo1.a_table t WHERE t.tableId = ?) WHERE ROW_NUMBER BETWEEN 0 AND 25";
        sel = new Select(table)
            .setParameter(new SelectParam(table.getPrimaryKey(), Operator.EQUALS, "1"))
            .setLimitParam(l)
            .setOrderParam(new OrderParam("name", "DESC"))
            .addColumn("a");

        doTest(sql, sel);
    }

    public void doTest (String expected, Object obj) {
        if (obj instanceof Select) {
            assertEquals(expected, d.toStatementString((Select) obj));
        } else if (obj instanceof Delete) {
            assertEquals(expected, d.toStatementString((Delete) obj));
        } else if (obj instanceof Insert) {
            assertEquals(expected, d.toStatementString((Insert) obj));
        } else if (obj instanceof Update) {
            assertEquals(expected, d.toStatementString((Update) obj));
        }
    }
}
