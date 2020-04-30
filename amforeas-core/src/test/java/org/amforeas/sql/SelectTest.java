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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.sql.Select;
import amforeas.sql.SelectParam;
import amforeas.sql.Table;

/**
 * Tests for {@link amforeas.sql.Select}
 */
@Tag("offline-tests")
public class SelectTest {

    private final Table table = new Table("database", "table", "xId");

    @Test
    public void testIncorrect () {
        assertThrows(IllegalArgumentException.class, () -> new Select(null));
    }

    @Test
    public void testContructor () {
        assertNotNull(new Select(table));
    }

    @Test
    public void test_isAllRecords () {
        Select s = new Select(table);
        assertTrue(s.isAllRecords());

        s.setParameter(new SelectParam("col1", "1"));
        assertFalse(s.isAllRecords());
    }

    @Test
    public void test_isAllColumns () {
        Select s = new Select(table);
        assertTrue(s.isAllColumns());

        s.addColumn("foo");
        assertFalse(s.isAllColumns());
    }


}
