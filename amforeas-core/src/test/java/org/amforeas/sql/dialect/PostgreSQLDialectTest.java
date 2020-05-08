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
import amforeas.sql.dialect.PostgreSQLDialect;

@Tag("dialect-tests")
public class PostgreSQLDialectTest extends SQLDialectTest {

    public PostgreSQLDialectTest() {
        d = new PostgreSQLDialect();
    }

    @Test
    @Override
    public void testDelete () {}

    @Test
    @Override
    public void testInsert () {}

    @Test
    @Override
    public void testSelect () {}

    @Test
    @Override
    public void testUpdate () {}

    @Test
    @Override
    public void testSelect_between () {}

    @Test
    @Override
    public void testSelect_like () {}

    @Test
    @Override
    public void testSelect_isNull () {}

    @Test
    @Override
    public void testSelect_isNotNull () {}

    @Test
    @Override
    public void test_rowCountStatement () {}

    @Test
    @Override
    public void testSelect_columns () {}


}
