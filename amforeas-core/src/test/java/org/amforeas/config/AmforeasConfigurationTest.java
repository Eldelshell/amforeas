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

package org.amforeas.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import amforeas.SingletonFactory;
import amforeas.acl.ACLRule;
import amforeas.config.AmforeasConfiguration;

@ExtendWith(MockitoExtension.class)
@Tag("offline-tests")
class AmforeasConfigurationTest {

    private AmforeasConfiguration conf;

    @Mock
    SingletonFactory factory;

    @BeforeEach
    public void setUpEach () {
        // This way we stop the configuration from setting up the database
        when(factory.getConfiguration()).thenReturn(new AmforeasConfigurationStub());
        conf = factory.getConfiguration();
        conf.loadProperties();
        conf.load();
    }

    @Test
    void test_getDatabaseConfigurations () {
        assertEquals("test_db", conf.getDatabaseConfiguration("test_db").getDatabase());
        assertNull(conf.getDatabaseConfiguration(null));
        assertNull(conf.getDatabaseConfiguration(""));
        assertNull(conf.getDatabaseConfiguration("invalid"));
    }

    @Test
    void test_getDatabaseConfigurationForAlias () {
        assertEquals("test_db", conf.getDatabaseConfigurationForAlias("alias1").getDatabase());
        assertThrows(IllegalArgumentException.class, () -> conf.getDatabaseConfigurationForAlias(null));
        assertThrows(IllegalArgumentException.class, () -> conf.getDatabaseConfigurationForAlias(""));
        assertThrows(IllegalArgumentException.class, () -> conf.getDatabaseConfigurationForAlias("invalid"));
    }

    @Test
    void test_getters () {
        assertEquals(8080, conf.getServerPort());
        assertEquals("/amforeas/*", conf.getServerRoot());
        assertEquals("0.0.0.0", conf.getServerHost());
        assertEquals(5, conf.getServerThreadsMin());
        assertEquals(10, conf.getServerThreadsMax());
        assertNull(conf.getSecurePort());
        assertNull(conf.getJKSFile());
        assertNull(conf.getJKSFilePassword());
    }

    @Test
    void test_getAliasRule () {
        /* default is always all */
        assertEquals(conf.getAliasRule("alias1"), ACLRule.of("alias1", "all"));

        assertEquals(conf.getAliasRule("alias2"), ACLRule.of("alias2", "none"));
        assertEquals(conf.getAliasRule("alias3"), ACLRule.of("alias3", "meta, read, update"));
    }

    @Test
    void test_getResourceRules () {
        /* this are the inherited */
        assertEquals(conf.getResourceRules("alias1", "notdefined"), ACLRule.of("alias1", "notdefined", "all"));
        assertEquals(conf.getResourceRules("alias2", "notdefined"), ACLRule.of("alias2", "notdefined", "none"));
        assertEquals(conf.getResourceRules("alias3", "notdefined"), ACLRule.of("alias3", "notdefined", "meta, read, update"));

        /* should match the one defined above */
        assertEquals(conf.getResourceRules("alias2", "cars"), ACLRule.of("alias2", "cars", "insert, delete"));
        assertEquals(conf.getResourceRules("alias3", "users"), ACLRule.of("alias3", "users", "none"));
    }


}
