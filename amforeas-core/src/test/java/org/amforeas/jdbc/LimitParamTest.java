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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.jdbc.LimitParam;

/**
 * Tests for {@link amforeas.jdbc.LimitParam}
 */
@Tag("offline-tests")
public class LimitParamTest {

    @Test
    public void testDefaults () {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        assertDefault(LimitParam.valueOf(params));
    }

    @Test
    public void testCorrect () {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("limit", "10");
        assertEquals(LimitParam.valueOf(params).getLimit(), 10);
        assertEquals(LimitParam.valueOf(params).getStart(), 0);

        params.add("offset", "20");
        assertEquals(LimitParam.valueOf(params).getLimit(), 10);
        assertEquals(LimitParam.valueOf(params).getStart(), 20);
    }

    @Test
    public void testIncorrect () {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("limit", "-1");
        assertDefault(LimitParam.valueOf(params));

        params.remove("limit");
        params.add("limit", "foo");
        assertDefault(LimitParam.valueOf(params));

        params.add("offset", "-1");
        assertDefault(LimitParam.valueOf(params));

        params.remove("offset");
        params.add("offset", "foo");
        assertDefault(LimitParam.valueOf(params));

        /* with an invalid limit, the offset is still the default */
        params.remove("offset");
        params.add("offset", "10");
        assertDefault(LimitParam.valueOf(params));

        /* with an invalid offset, limit works and offset is default */
        params = new MultivaluedHashMap<>();
        params.add("limit", "10");
        params.add("offset", "foo");
        assertEquals(LimitParam.valueOf(params).getLimit(), 10);
        assertEquals(LimitParam.valueOf(params).getStart(), 0);

        /* if the limit is too big */
        params.clear();
        params.add("limit", "1001");
        assertEquals(LimitParam.valueOf(params).getLimit(), 1000);
    }

    private void assertDefault (LimitParam lp) {
        assertNotNull(lp);
        assertTrue(lp.getLimit().equals(25));
        assertTrue(lp.getStart().equals(0));
    }

    @Test
    public void testPage () {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("page", "1");
        assertEquals(LimitParam.valueOf(params, 25).getLimit(), 25);
        assertEquals(LimitParam.valueOf(params, 25).getStart(), 0);

        params.remove("page");
        params.add("page", "2");
        assertEquals(LimitParam.valueOf(params, 25).getLimit(), 25);
        assertEquals(LimitParam.valueOf(params, 25).getStart(), 25);

        params.remove("page");
        params.add("page", "foo");
        assertEquals(LimitParam.valueOf(params, 25).getLimit(), 25);
        assertEquals(LimitParam.valueOf(params, 25).getStart(), 0);

        params.remove("page");
        params.add("page", "4");
        assertEquals(LimitParam.valueOf(params, 100).getLimit(), 100);
        assertEquals(LimitParam.valueOf(params, 100).getStart(), 300);
    }

}
