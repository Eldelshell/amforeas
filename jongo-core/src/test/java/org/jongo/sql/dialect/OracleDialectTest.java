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
package org.amforeas.sql.dialect;

import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;
import amforeas.sql.*;
import amforeas.sql.dialect.OracleDialect;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Tests for the OracleDialect
 * @author Alejandro Ayuso 
 */
public class OracleDialectTest extends SQLDialectTest {
    
    public OracleDialectTest() {
        d = new OracleDialect();
    }

    @Test
    @Override
    public void testSelect() {
        doTest("SELECT t.* FROM demo1.a_table t", new Select(table));
        
        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId = ?", new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), "1")));
        
        doTest("SELECT t.* FROM demo1.a_table t WHERE t.name = ?", new Select(table).setParameter(new SelectParam("name", "1")));
        
        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId = ? ORDER BY t.tableId ASC",
                new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), "1")).setOrderParam(new OrderParam(table)));
        
        doTest("SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY t.tableId ) AS ROW_NUMBER, t.* FROM demo1.a_table t WHERE t.name = ?) WHERE ROW_NUMBER BETWEEN 0 AND 25",
                new Select(table).setParameter(new SelectParam("name", "1")).setLimitParam(new LimitParam()));
        
        doTest("SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY t.name DESC ) AS ROW_NUMBER, t.* FROM demo1.a_table t WHERE t.tableId = ?) WHERE ROW_NUMBER BETWEEN 0 AND 25",
                new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), "1")).setLimitParam(l).setOrderParam(new OrderParam("name", "DESC")));
    }

    @Test
    @Override
    public void testDelete() {
        doTest("DELETE FROM demo1.a_table WHERE a_table.tableId=?", new Delete(table).setId("1"));
        doTest("DELETE FROM demo1.grrr WHERE grrr.id=?", new Delete(new Table("demo1", "grrr")).setId("1"));
    }

    @Test
    @Override
    public void testInsert(){
        doTest("INSERT INTO demo1.a_table (name,age) VALUES (?,?)", new Insert(table).addColumn("name", "foo bar").addColumn("age", "50"));
    }
    
    @Test
    @Override
    public void testUpdate(){
        doTest("UPDATE demo1.a_table SET a_table.name=?,a_table.age=? WHERE a_table.tableId=?", new Update(table).setId("1").addColumn("name", "foo bar").addColumn("age", "50"));
        doTest("UPDATE demo1.grrr SET grrr.name=? WHERE grrr.id=?", new Update(new Table("demo1","grrr")).setId("1").addColumn("name", "foo bar"));
    }
    
    @Test
    public void testDynamicFinders(){
        System.out.println(d.toStatementString(new DynamicFinder("test", "findAllBy", "Name"), l,o));
        Assert.assertEquals("SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY test.tableId ASC ) AS ROW_NUMBER, test.* FROM test WHERE  name = ? ) WHERE ROW_NUMBER BETWEEN 0 AND 25",
                d.toStatementString(new DynamicFinder("test", "findAllBy", "Name"), l,o));
    }
}
