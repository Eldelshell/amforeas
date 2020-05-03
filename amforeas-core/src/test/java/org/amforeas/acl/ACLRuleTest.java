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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.acl.ACLRule;

@Tag("offline-tests")
public class ACLRuleTest {


    @Test
    public void testEquals () {
        assertEquals(ACLRule.of("alias1", "all"), ACLRule.of("alias1", "all"));
        assertEquals(ACLRule.of("alias1", "read, meta"), ACLRule.of("alias1", "meta, read"));
        assertEquals(ACLRule.of("alias1", "meta, insert, read"), ACLRule.of("alias1", "meta, insert, read"));

        assertNotEquals(ACLRule.of("alias1", "all"), ACLRule.of("alias2", "all"));
        assertNotEquals(ACLRule.of("alias1", "read, meta"), ACLRule.of("alias1", "meta, read, insert"));
        assertNotEquals(ACLRule.of("alias1", "meta, insert, read"), ACLRule.of("alias1", "meta"));

        assertEquals(ACLRule.of("alias1", "r1", "all"), ACLRule.of("alias1", "r1", "all"));
        assertEquals(ACLRule.of("alias1", "r1", "read, meta"), ACLRule.of("alias1", "r1", "meta, read"));
        assertEquals(ACLRule.of("alias1", "r1", "meta, insert, read"), ACLRule.of("alias1", "r1", "meta, insert, read"));

        assertNotEquals(ACLRule.of("alias1", "r1", "all"), ACLRule.of("alias1", "r2", "all"));
        assertNotEquals(ACLRule.of("alias1", "r1", "all"), ACLRule.of("alias1", "r1", "none"));
        assertNotEquals(ACLRule.of("alias1", "r1", "read, meta"), ACLRule.of("alias1", "r1", "meta, read, insert"));
        assertNotEquals(ACLRule.of("alias1", "r1", "meta, insert, read"), ACLRule.of("alias1", "r1", "meta, insert"));
    }
}
