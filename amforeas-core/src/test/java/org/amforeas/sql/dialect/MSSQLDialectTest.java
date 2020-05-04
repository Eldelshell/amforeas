/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
 *
 * This file is part of Amforeas. Amforeas is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Amforeas is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Amforeas. If not, see <http://www.gnu.org/licenses/>.
 */
package org.amforeas.sql.dialect;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.sql.dialect.MSSQLDialect;

@Tag("dialect-tests")
public class MSSQLDialectTest extends SQLDialectTest {

    public MSSQLDialectTest() {
        d = new MSSQLDialect();
    }

    @Test
    @Override
    public void testDelete () {
        // TODO Implement
    }

    @Test
    @Override
    public void testInsert () {
        // TODO Implement
    }

    @Test
    @Override
    public void testSelect () {
        // TODO Implement
    }

    @Test
    @Override
    public void testUpdate () {
        // TODO Implement
    }

    @Test
    @Override
    public void testSelect_between () {
        // TODO Auto-generated method stub
    }

    @Test
    @Override
    public void testSelect_like () {
        // TODO Auto-generated method stub
    }

    @Test
    @Override
    public void testSelect_isNull () {
        // TODO Auto-generated method stub
    }

    @Test
    @Override
    public void testSelect_isNotNull () {
        // TODO Auto-generated method stub
    }
}
