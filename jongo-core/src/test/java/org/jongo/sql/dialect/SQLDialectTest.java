package org.amforeas.sql.dialect;

import amforeas.enums.Operator;
import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;
import amforeas.sql.*;
import amforeas.sql.dialect.Dialect;
import amforeas.sql.dialect.SQLDialect;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alejandro Ayuso
 */
public class SQLDialectTest {
    
    private static final Logger log = LoggerFactory.getLogger(SQLDialectTest.class);
    
    Table table = new Table("demo1", "a_table", "tableId");
    Dialect d;
    
    OrderParam o = new OrderParam(table);
    LimitParam l = new LimitParam();
    
    public SQLDialectTest(){
        d = new SQLDialect();
    }
    
    @Test
    public void testSelect() {
        doTest("SELECT t.* FROM demo1.a_table t", new Select(table));
        
        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId = ?", new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), Operator.EQUALS, "1")));
        
        doTest("SELECT t.* FROM demo1.a_table t WHERE t.name = ?", new Select(table).setParameter(new SelectParam("name", Operator.EQUALS, "1")));
        
        doTest("SELECT t.* FROM demo1.a_table t WHERE t.tableId = ? ORDER BY t.tableId ASC", 
                new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), Operator.EQUALS, "1")).setOrderParam(new OrderParam(table)));
        
        doTest("SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY t.tableId ) AS ROW_NUMBER, t.* FROM demo1.a_table t WHERE t.name = ?) WHERE ROW_NUMBER BETWEEN 0 AND 25",
                new Select(table).setParameter(new SelectParam("name", Operator.EQUALS, "1")).setLimitParam(new LimitParam()));
        
        doTest("SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY t.name DESC ) AS ROW_NUMBER, t.* FROM demo1.a_table t WHERE t.tableId = ?) WHERE ROW_NUMBER BETWEEN 0 AND 25",
                new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), Operator.EQUALS, "1")).setLimitParam(l).setOrderParam(new OrderParam("name", "DESC")));
    }
    
    @Test
    public void testDelete(){
        doTest("DELETE FROM a_table WHERE tableId=?", new Delete(table).setId("1"));
        doTest("DELETE FROM grrr WHERE id=?", new Delete(new Table("demo1", "grrr")).setId("1"));
    }
    
    @Test
    public void testInsert(){
        doTest("INSERT INTO a_table (name,age) VALUES (?,?)", new Insert(table).addColumn("name", "foo bar").addColumn("age", "50"));
    }
    
    @Test
    public void testUpdate(){
        doTest("UPDATE a_table SET name=?,age=? WHERE tableId=?", new Update(table).setId("1").addColumn("name", "foo bar").addColumn("age", "50"));
        doTest("UPDATE grrr SET name=? WHERE id=?", new Update(new Table("demo1","grrr")).setId("1").addColumn("name", "foo bar"));
    }
    
    public void doTest(String expected, Object obj){
        log.debug(expected);
        if(obj instanceof Select){
            Assert.assertEquals(expected, d.toStatementString((Select)obj));
        }else if(obj instanceof Delete){
            Assert.assertEquals(expected, d.toStatementString((Delete)obj));
        }else if(obj instanceof Insert){
            Assert.assertEquals(expected, d.toStatementString((Insert)obj));
        }else if(obj instanceof Update){
            Assert.assertEquals(expected, d.toStatementString((Update)obj));
        }
    }
}
