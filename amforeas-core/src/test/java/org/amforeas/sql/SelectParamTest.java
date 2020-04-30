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

package org.amforeas.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.enums.Operator;
import amforeas.sql.SelectParam;

/**
 * Tests for {@link amforeas.sql.SelectParam}
 */
@Tag("offline-tests")
public class SelectParamTest {

    @Test
    public void testIncorrect () {
        assertThrows(IllegalArgumentException.class, () -> new SelectParam("", ""));
        assertThrows(IllegalArgumentException.class, () -> new SelectParam("id", null));
        assertThrows(IllegalArgumentException.class, () -> new SelectParam(null, "id"));
        assertThrows(IllegalArgumentException.class, () -> new SelectParam("id", null, "1"));
        assertThrows(IllegalArgumentException.class, () -> new SelectParam("id", Operator.AND, "1"));
        assertThrows(IllegalArgumentException.class, () -> new SelectParam("id", Operator.OR, "1"));

        SelectParam p = new SelectParam("id", "1");
        assertThrows(IllegalArgumentException.class, () -> p.setOperator(Operator.OR));
        assertThrows(IllegalArgumentException.class, () -> p.setOperator(Operator.AND));
    }

    @Test
    public void testContructor () {
        assertNotNull(new SelectParam("id", "1"));
        assertNotNull(new SelectParam("id", Operator.EQUALS, "1"));
        assertNotNull(new SelectParam("id", Operator.ISNULL, "1"));
        assertNotNull(new SelectParam("id", Operator.ISNOTNULL, "1"));
    }

    @Test
    public void testSQl () {
        SelectParam p = new SelectParam("id", "1");
        assertEquals("id = ?", p.sql());

        p.setOperator(Operator.GREATERTHAN);
        assertEquals("id > ?", p.sql());

        p = new SelectParam("id", Operator.BETWEEN, "1", "2");
        assertEquals("id BETWEEN ? AND ?", p.sql());

        p = new SelectParam("id", Operator.LIKE, "1");
        assertEquals("id LIKE ?", p.sql());

        p.setOperator(Operator.ISNOTNULL);
        assertEquals("id IS NOT NULL", p.sql());

        p.setOperator(Operator.ISNULL);
        assertEquals("id IS NULL", p.sql());

        p.setOperator(Operator.ILIKE);
        assertEquals("id ILIKE ?", p.sql());
    }

}
