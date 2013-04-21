/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
 *
 * This file is part of Jongo.
 * Jongo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * Jongo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jongo.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jongo.jdbc;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MultivaluedMap;

import jongo.jdbc.OrderParam;

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Alejandro Ayuso 
 */
public class OrderParamTest {
    
    public OrderParamTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testSomeMethod() {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        OrderParam op = OrderParam.valueOf(formParams);
        assertNotNull(op);
        assertTrue(op.getColumn().equals("id"));
        assertTrue(op.getDirection().equals("ASC"));
        
        formParams.add("dir", "DESC");
        op = OrderParam.valueOf(formParams);
        assertNotNull(op);
        assertTrue(op.getColumn().equals("id"));
        assertEquals("DESC", op.getDirection());
        
        op = OrderParam.valueOf(formParams, "tableId");
        assertNotNull(op);
        assertTrue(op.getColumn().equals("tableId"));
        assertEquals("DESC", op.getDirection());
        
        formParams.add("sort", "anotherColumn");
        op = OrderParam.valueOf(formParams, "tableId");
        assertNotNull(op);
        assertEquals("anotherColumn", op.getColumn());
        assertEquals("DESC", op.getDirection());
        
        formParams = new MultivaluedMapImpl();
        formParams.add("sort", "anotherColumn");
        op = OrderParam.valueOf(formParams);
        assertNotNull(op);
        assertEquals("anotherColumn", op.getColumn());
        assertEquals("ASC", op.getDirection());
        
        formParams = new MultivaluedMapImpl();
        formParams.add("customId", "anotherColumn");
        op = OrderParam.valueOf(formParams, "anotherColumn");
        assertNotNull(op);
        assertEquals("anotherColumn", op.getColumn());
        assertEquals("ASC", op.getDirection());
    }
}
