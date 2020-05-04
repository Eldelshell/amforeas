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

package org.amforeas.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.cache.SimpleCache;

@Tag("offline-tests")
public class SimpleCacheTest {

    @Test
    public void testCache () {
        final String key = "k";
        SimpleCache<String, Integer> c = new SimpleCache<>(100);

        c.put(key, 10);

        assertTrue(c.get(key).isPresent());
        assertEquals(c.get(key).get(), 10);

        try {
            Thread.sleep(100);
            assertTrue(c.get(key).isEmpty());
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }

        c.put(key, 20);
        assertTrue(c.get(key).isPresent());
        assertEquals(c.get(key).get(), 20);
    }
}
