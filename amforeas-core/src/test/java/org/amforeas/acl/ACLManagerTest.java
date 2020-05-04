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

package org.amforeas.acl;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import org.amforeas.config.AmforeasConfigurationStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import amforeas.SingletonFactory;
import amforeas.acl.ACLFilter;
import amforeas.acl.ACLManager;
import amforeas.config.AmforeasConfiguration;

@ExtendWith(MockitoExtension.class)
@Tag("offline-tests")
public class ACLManagerTest {

    private AmforeasConfiguration conf;

    private ACLManager manager;

    @Mock
    SingletonFactory factory;

    @BeforeEach
    public void setUpEach () {
        // This way we stop the configuration from setting up the database
        when(factory.getConfiguration()).thenReturn(new AmforeasConfigurationStub());
        conf = factory.getConfiguration();
        conf.loadProperties();
        conf.load();

        manager = new ACLManager(factory);
    }

    @Test
    void test_validateAlias_1_Rules () {
        String alias = "alias1";
        assertTrue(manager.validate(alias, ACLFilter.META));
        assertTrue(manager.validate(alias, ACLFilter.READ));
        assertTrue(manager.validate(alias, ACLFilter.UPDATE));
        assertTrue(manager.validate(alias, ACLFilter.DELETE));
        assertTrue(manager.validate(alias, ACLFilter.INSERT));
        assertTrue(manager.validate(alias, ACLFilter.EXEC));
    }

    @Test
    void test_validateAlias_2_Rules () {
        String alias = "alias2";
        assertFalse(manager.validate(alias, ACLFilter.META));
        assertFalse(manager.validate(alias, ACLFilter.READ));
        assertFalse(manager.validate(alias, ACLFilter.UPDATE));
        assertFalse(manager.validate(alias, ACLFilter.DELETE));
        assertFalse(manager.validate(alias, ACLFilter.INSERT));
        assertFalse(manager.validate(alias, ACLFilter.EXEC));
    }

    @Test
    void test_validateAlias_3_Rules () {
        String alias = "alias3";
        assertTrue(manager.validate(alias, ACLFilter.META));
        assertTrue(manager.validate(alias, ACLFilter.READ));
        assertTrue(manager.validate(alias, ACLFilter.UPDATE));
        assertFalse(manager.validate(alias, ACLFilter.DELETE));
        assertFalse(manager.validate(alias, ACLFilter.INSERT));
        assertFalse(manager.validate(alias, ACLFilter.EXEC));
    }

    @Test
    void test_validateAlias_4_Rules () {
        String alias = "alias4";
        assertTrue(manager.validate(alias, ACLFilter.META));
        assertFalse(manager.validate(alias, ACLFilter.READ));
        assertFalse(manager.validate(alias, ACLFilter.UPDATE));
        assertFalse(manager.validate(alias, ACLFilter.DELETE));
        assertFalse(manager.validate(alias, ACLFilter.INSERT));
        assertTrue(manager.validate(alias, ACLFilter.EXEC));
    }

    @Test
    void test_validateAlias_1_ResourceRules () {
        String alias = "alias1";
        String resource = "doesntmatter";
        assertTrue(manager.validate(alias, resource, ACLFilter.META));
        assertTrue(manager.validate(alias, resource, ACLFilter.READ));
        assertTrue(manager.validate(alias, resource, ACLFilter.UPDATE));
        assertTrue(manager.validate(alias, resource, ACLFilter.DELETE));
        assertTrue(manager.validate(alias, resource, ACLFilter.INSERT));
    }

    @Test
    void test_validateAlias_2_ResourceRules_notSet () {
        String alias = "alias2";
        String resource = "doesntmatter";
        assertFalse(manager.validate(alias, resource, ACLFilter.META));
        assertFalse(manager.validate(alias, resource, ACLFilter.READ));
        assertFalse(manager.validate(alias, resource, ACLFilter.UPDATE));
        assertFalse(manager.validate(alias, resource, ACLFilter.DELETE));
        assertFalse(manager.validate(alias, resource, ACLFilter.INSERT));
    }

    @Test
    void test_validateAlias_2_ResourceRules_set () {
        String alias = "alias2";
        String resource = "cars";
        assertFalse(manager.validate(alias, resource, ACLFilter.META));
        assertFalse(manager.validate(alias, resource, ACLFilter.READ));
        assertFalse(manager.validate(alias, resource, ACLFilter.UPDATE));
        assertTrue(manager.validate(alias, resource, ACLFilter.DELETE));
        assertTrue(manager.validate(alias, resource, ACLFilter.INSERT));
    }

    @Test
    void test_validateAlias_3_ResourceRules_notSet () {
        String alias = "alias3";
        String resource = "doesntmatter";
        assertTrue(manager.validate(alias, resource, ACLFilter.META));
        assertTrue(manager.validate(alias, resource, ACLFilter.READ));
        assertTrue(manager.validate(alias, resource, ACLFilter.UPDATE));
        assertFalse(manager.validate(alias, resource, ACLFilter.DELETE));
        assertFalse(manager.validate(alias, resource, ACLFilter.INSERT));
    }

    @Test
    void test_validateAlias_3_ResourceRules_setLocked () {
        String alias = "alias3";
        String resource = "users";
        assertFalse(manager.validate(alias, resource, ACLFilter.META));
        assertFalse(manager.validate(alias, resource, ACLFilter.READ));
        assertFalse(manager.validate(alias, resource, ACLFilter.UPDATE));
        assertFalse(manager.validate(alias, resource, ACLFilter.DELETE));
        assertFalse(manager.validate(alias, resource, ACLFilter.INSERT));
    }

    @Test
    void test_validateAlias_3_ResourceRules_setPartial () {
        String alias = "alias3";
        String resource = "movies";
        assertFalse(manager.validate(alias, resource, ACLFilter.META));
        assertFalse(manager.validate(alias, resource, ACLFilter.READ));
        assertFalse(manager.validate(alias, resource, ACLFilter.UPDATE));
        assertTrue(manager.validate(alias, resource, ACLFilter.DELETE));
        assertTrue(manager.validate(alias, resource, ACLFilter.INSERT));
    }

    @Test
    void test_validateAlias_3_ResourceRules_setAll () {
        String alias = "alias3";
        String resource = "cats";
        assertTrue(manager.validate(alias, resource, ACLFilter.META));
        assertTrue(manager.validate(alias, resource, ACLFilter.READ));
        assertTrue(manager.validate(alias, resource, ACLFilter.UPDATE));
        assertTrue(manager.validate(alias, resource, ACLFilter.DELETE));
        assertTrue(manager.validate(alias, resource, ACLFilter.INSERT));
    }

    @Test
    void test_validateAlias_4_ResourceRules_notSet () {
        String alias = "alias4";
        String resource = "doesntmatter";
        /* since this alias only have exec and meta */
        assertTrue(manager.validate(alias, resource, ACLFilter.META));
        assertFalse(manager.validate(alias, resource, ACLFilter.READ));
        assertFalse(manager.validate(alias, resource, ACLFilter.UPDATE));
        assertFalse(manager.validate(alias, resource, ACLFilter.DELETE));
        assertFalse(manager.validate(alias, resource, ACLFilter.INSERT));
    }

}
