/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
 *
 * This file is part of Jongo.
 * Jongo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * Jongo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jongo.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jongo.sql.dialect;

import jongo.sql.dialect.PostgreSQLDialect;

import org.junit.Test;

public class PostgreSQLDialectTest extends SQLDialectTest {
    
    public PostgreSQLDialectTest() {
        d = new PostgreSQLDialect();
    }

    @Test
    @Override
    public void testDelete() {
        // TODO Implement
    }

    @Test
    @Override
    public void testInsert() {
        // TODO Implement
    }

    @Test
    @Override
    public void testSelect() {
        // TODO Implement
    }

    @Test
    @Override
    public void testUpdate() {
        // TODO Implement
    }
}
