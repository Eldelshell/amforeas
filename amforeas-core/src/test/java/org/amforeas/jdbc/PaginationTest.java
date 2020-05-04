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
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.jdbc.LimitParam;
import amforeas.rest.xstream.Pagination;

@Tag("offline-tests")
public class PaginationTest {

    @Test
    public void test_ofLimit () {
        assertEquals(Pagination.of(new LimitParam(25, 0)).getSize(), 25);
        assertEquals(Pagination.of(new LimitParam(25, 0)).getPage(), 1);

        assertEquals(Pagination.of(new LimitParam(100, 50)).getSize(), 50);
        assertEquals(Pagination.of(new LimitParam(100, 50)).getPage(), 2);

        assertEquals(Pagination.of(new LimitParam(225, 150)).getSize(), 75);
        assertEquals(Pagination.of(new LimitParam(225, 150)).getPage(), 3);
        assertEquals(Pagination.of(new LimitParam(300, 225)).getPage(), 4);
    }

    @Test
    public void test_ofLimitWithTotal100 () {
        LimitParam limit = new LimitParam(25, 0);
        Integer total = 100;
        Pagination p = Pagination.of(limit, total);

        assertEquals(p.getSize(), 25);
        assertEquals(p.getPage(), 1);
        assertEquals(p.getTotal(), total);
        assertEquals(p.getPages(), 4);
    }

    @Test
    public void test_ofLimitWithTotal1 () {
        LimitParam limit = new LimitParam(25, 0);
        Integer total = 1;
        Pagination p = Pagination.of(limit, total);

        assertEquals(p.getSize(), 25);
        assertEquals(p.getPage(), 1);
        assertEquals(p.getTotal(), total);
        assertEquals(p.getPages(), 1);
    }

    @Test
    public void test_ofLimitWithEmpty () {
        LimitParam limit = new LimitParam(25, 0);
        Integer total = 0;
        Pagination p = Pagination.of(limit, total);

        assertEquals(p.getSize(), 25);
        assertEquals(p.getPage(), 1);
        assertNull(p.getTotal());
        assertNull(p.getPages());
    }

    @Test
    public void test_ofLimitWithTotalEqualsSize () {
        LimitParam limit = new LimitParam(25, 0);
        Integer total = 25;
        Pagination p = Pagination.of(limit, total);

        assertEquals(p.getSize(), 25);
        assertEquals(p.getPage(), 1);
        assertEquals(p.getTotal(), total);
        assertEquals(p.getPages(), 1);
    }

    @Test
    public void test_ofLimitWithTotal26 () {
        LimitParam limit = new LimitParam(25, 0);
        Integer total = 26;
        Pagination p = Pagination.of(limit, total);

        assertEquals(p.getSize(), 25);
        assertEquals(p.getPage(), 1);
        assertEquals(p.getTotal(), total);
        assertEquals(p.getPages(), 2);
    }

}
