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

package org.amforeas.acl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.acl.ACLFilter;

@Tag("offline-tests")
public class ACLFilterTest {

    @Test
    public void testParse () {
        assertTrue(ACLFilter.parse("").isEmpty());
        assertTrue(ACLFilter.parse(null).isEmpty());

        assertThrows(IllegalArgumentException.class, () -> ACLFilter.parse("invalid"));
        assertThrows(IllegalArgumentException.class, () -> ACLFilter.parse("all, invalid"));
        assertThrows(IllegalArgumentException.class, () -> ACLFilter.parse("all, none"));
        assertThrows(IllegalArgumentException.class, () -> ACLFilter.parse("all, meta"));
        assertThrows(IllegalArgumentException.class, () -> ACLFilter.parse("none, meta"));

        assertEquals(ACLFilter.parse("all").size(), 1);
        assertEquals(ACLFilter.parse("all,all").size(), 1);
        assertEquals(ACLFilter.parse("all, all").size(), 1);
        assertEquals(ACLFilter.parse("all,all").size(), 1);
        assertEquals(ACLFilter.parse("meTa, Read, inSerT, UPDATE, delete").size(), 5);
    }

}
