/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
 *
 * This file is part of Amforeas.
 * Amforeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * Amforeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Amforeas.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amforeas.sql.dialect;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Alejandro Ayuso <alejandroayuso@gmail.com>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    org.amforeas.sql.dialect.SQLDialectTest.class,
    org.amforeas.sql.dialect.OracleDialectTest.class, 
    org.amforeas.sql.dialect.PostgreSQLDialectTest.class, 
    org.amforeas.sql.dialect.HSQLDialectTest.class, 
    org.amforeas.sql.dialect.MSSQLDialectTest.class, 
    org.amforeas.sql.dialect.MySQLDialectTest.class
})
public class DialectsTestSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
}
