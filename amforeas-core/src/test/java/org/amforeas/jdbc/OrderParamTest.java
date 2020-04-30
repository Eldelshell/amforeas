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
package org.amforeas.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.jdbc.OrderParam;

/**
 * Tests for {@link amforeas.jdbc.OrderParam}
 */
@Tag("offline-tests")
public class OrderParamTest {

    @Test
    public void testDefaults () {
        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<>();
        OrderParam op = OrderParam.valueOf(formParams);
        assertNotNull(op);
        assertTrue(op.getColumn().equals("id"));
        assertTrue(op.getDirection().equals(OrderParam.ASC));
    }

    @Test
    public void testCorrect () {
        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<>();
        formParams.add("dir", OrderParam.DESC);

        /* with default primary key */
        OrderParam op = OrderParam.valueOf(formParams);
        assertNotNull(op);
        assertTrue(op.getColumn().equals("id"));
        assertEquals(OrderParam.DESC, op.getDirection());

        /* with primary key */
        op = OrderParam.valueOf(formParams, "tableId");
        assertNotNull(op);
        assertTrue(op.getColumn().equals("tableId"));
        assertEquals(OrderParam.DESC, op.getDirection());

        /* with a sort & order (desc) set previously*/
        formParams.add("sort", "anotherColumn");
        op = OrderParam.valueOf(formParams, "tableId");
        assertNotNull(op);
        assertEquals("anotherColumn", op.getColumn());
        assertEquals(OrderParam.DESC, op.getDirection());

        /* sort with default order (asc) */
        formParams = new MultivaluedHashMap<>();
        formParams.add("sort", "anotherColumn");
        op = OrderParam.valueOf(formParams);
        assertNotNull(op);
        assertEquals("anotherColumn", op.getColumn());
        assertEquals(OrderParam.ASC, op.getDirection());

        /* ignore customId param */
        formParams = new MultivaluedHashMap<>();
        formParams.add("customId", "foo");
        op = OrderParam.valueOf(formParams, "anotherColumn");
        assertNotNull(op);
        assertEquals("anotherColumn", op.getColumn());
        assertEquals(OrderParam.ASC, op.getDirection());
    }

    @Test
    public void testIncorrect () {
        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<>();

        /* invalid direction */
        formParams.add("dir", "INVALID");
        assertThrows(IllegalArgumentException.class, () -> OrderParam.valueOf(formParams));
    }
}
