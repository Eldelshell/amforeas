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
    org.jongo.sql.dialect.SQLDialectTest.class,
    org.jongo.sql.dialect.OracleDialectTest.class, 
    org.jongo.sql.dialect.PostgreSQLDialectTest.class, 
    org.jongo.sql.dialect.HSQLDialectTest.class, 
    org.jongo.sql.dialect.MSSQLDialectTest.class, 
    org.jongo.sql.dialect.MySQLDialectTest.class
})
public class DialectsTestSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
}
