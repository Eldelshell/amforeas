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
package org.amforeas;

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
    org.amforeas.UtilsTest.class, 
    org.amforeas.RestControllerTest.class, 
    org.amforeas.JDBCExecutorTest.class, 
    org.amforeas.XmlXstreamTest.class, 
    org.amforeas.DynamicFinderTest.class,
    org.amforeas.jdbc.OrderParamTest.class,
    org.amforeas.sql.dialect.DialectsTestSuite.class
})
public class JongoOfflineTestSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
}
