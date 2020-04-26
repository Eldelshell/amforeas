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
package org.amforeas.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.jdbc.OrderParam;

/**
 *
 * @author Alejandro Ayuso
 */
@Tag("offline-tests")
public class OrderParamTest {

    public OrderParamTest() {}

    @BeforeAll
    public static void setUpClass () throws Exception {}

    @AfterAll
    public static void tearDownClass () throws Exception {}

    @Test
    public void testSomeMethod () {
        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<>();
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

        formParams = new MultivaluedHashMap<>();
        formParams.add("sort", "anotherColumn");
        op = OrderParam.valueOf(formParams);
        assertNotNull(op);
        assertEquals("anotherColumn", op.getColumn());
        assertEquals("ASC", op.getDirection());

        formParams = new MultivaluedHashMap<>();
        formParams.add("customId", "anotherColumn");
        op = OrderParam.valueOf(formParams, "anotherColumn");
        assertNotNull(op);
        assertEquals("anotherColumn", op.getColumn());
        assertEquals("ASC", op.getDirection());
    }
}
