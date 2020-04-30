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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.sql.Table;

/**
 * Tests for {@link amforeas.sql.Table}
 */
@Tag("offline-tests")
public class TableTest {

    @Test
    public void testIncorrect () {
        assertThrows(IllegalArgumentException.class, () -> new Table("", ""));
        assertThrows(IllegalArgumentException.class, () -> new Table("foo", null));
        assertThrows(IllegalArgumentException.class, () -> new Table(null, "foo"));
        assertThrows(IllegalArgumentException.class, () -> new Table("foo", null, "pk"));
        assertThrows(IllegalArgumentException.class, () -> new Table(null, "foo", "pk"));
    }

    @Test
    public void testContructor () {
        assertNotNull(new Table("database", "table"));
        assertNotNull(new Table("database", "table", null));
        assertNotNull(new Table("database", "table", "xId"));
    }

    @Test
    public void testDefaults () {
        Table t = new Table("database", "table");
        assertNotNull(t);
        assertEquals(t.getPrimaryKey(), "id");

        t = new Table("database", "table", null);
        assertNotNull(t);
        assertEquals(t.getPrimaryKey(), "id");
    }

    @Test
    public void testEquals () {
        Table t1 = new Table("database", "table");
        Table t2 = new Table("database", "table", null);
        Table t3 = new Table("database", "table", "xId");

        assertEquals(t1, t2);
        assertNotEquals(t1, t3);
        assertNotEquals(t2, t3);
    }

}
