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
package org.jongo;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Alejandro Ayuso 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    org.jongo.UtilsTest.class, 
    org.jongo.RestControllerTest.class, 
    org.jongo.JDBCExecutorTest.class, 
    org.jongo.XmlXstreamTest.class, 
    org.jongo.DynamicFinderTest.class,
    org.jongo.jdbc.OrderParamTest.class,
    org.jongo.sql.dialect.DialectsTestSuite.class
})
public class JongoOfflineTestSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
}
