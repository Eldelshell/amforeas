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

package org.amforeas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import java.sql.SQLException;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import amforeas.DefaultRestService;
import amforeas.RestController;
import amforeas.SingletonFactory;
import amforeas.acl.ACLRule;
import amforeas.config.AmforeasConfiguration;
import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;
import amforeas.rest.xstream.SuccessResponse;

@ExtendWith(MockitoExtension.class)
@Tag("offline-tests")
public class DefaultRestServiceTest {

    @Mock
    SingletonFactory factory;

    @Mock
    AmforeasConfiguration configuration;

    @Mock
    RestController controller;

    private AmforeasConfiguration conf;

    @BeforeEach
    public void setUpEach () {
        // This way we stop the configuration from setting up the database
        doNothing().when(configuration).load();
        doNothing().when(configuration).loadProperties();

        when(factory.getConfiguration()).thenReturn(configuration);
        when(factory.getRESTController(anyString())).thenReturn(controller);

        conf = factory.getConfiguration();
        conf.loadProperties();
        conf.load();
    }

    @Test
    public void test_dbMeta_acl () {
        when(controller.getDatabaseMetadata()).thenReturn(new SuccessResponse());
        when(configuration.getAliasRule("alias1")).thenReturn(ACLRule.of("alias1", "all"));
        when(configuration.getAliasRule("alias2")).thenReturn(ACLRule.of("alias2", "exec"));
        when(configuration.getAliasRule("alias3")).thenReturn(ACLRule.of("alias3", "none"));

        DefaultRestService service = new DefaultRestService(factory);
        assertEquals(service.dbMeta("alias1").getStatus(), 200);
        assertEquals(service.dbMeta("alias2").getStatus(), 405);
        assertEquals(service.dbMeta("alias3").getStatus(), 405);
    }

    @Test
    public void test_dbMeta_errors () {
        doThrow(new IllegalArgumentException()).when(controller).getDatabaseMetadata();
        when(configuration.getAliasRule(anyString())).thenReturn(ACLRule.of("alias1", "all"));

        DefaultRestService service = new DefaultRestService(factory);
        assertEquals(service.dbMeta("alias1").getStatus(), 400);
    }

    @Test
    public void test_resourceMeta_acl () {
        when(controller.getResourceMetadata(anyString())).thenReturn(new SuccessResponse());
        when(configuration.getResourceRules("alias1", "foo")).thenReturn(ACLRule.of("alias1", "foo", "all"));
        when(configuration.getResourceRules("alias1", "bar")).thenReturn(ACLRule.of("alias1", "bar", "none"));

        DefaultRestService service = new DefaultRestService(factory);
        assertEquals(service.resourceMeta("alias1", "foo").getStatus(), 200);
        assertEquals(service.resourceMeta("alias1", "bar").getStatus(), 405);
    }

    @Test
    public void test_resourceMeta__errors () throws SQLException {
        doThrow(new IllegalArgumentException()).when(controller).getResourceMetadata(anyString());
        when(configuration.getResourceRules(anyString(), anyString())).thenReturn(ACLRule.of("alias1", "all"));

        DefaultRestService service = new DefaultRestService(factory);
        assertEquals(service.resourceMeta("alias1", "foo").getStatus(), 400);
    }

    @Test
    public void test_get_acl () {
        when(controller.getResource(anyString(), anyString(), anyString(), any(LimitParam.class), any(OrderParam.class))).thenReturn(new SuccessResponse());

        when(configuration.getResourceRules("alias1", "foo")).thenReturn(ACLRule.of("alias1", "foo", "all"));
        when(configuration.getResourceRules("alias1", "bar")).thenReturn(ACLRule.of("alias1", "bar", "none"));

        MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();

        DefaultRestService service = new DefaultRestService(factory);
        assertEquals(service.get("alias1", "foo", "id", "1", queryParams).getStatus(), 200);
        assertEquals(service.get("alias1", "bar", "id", "1", queryParams).getStatus(), 405);
    }

    @Test
    public void test_get_errors () {
        doThrow(new IllegalArgumentException()).when(controller).getResource(anyString(), anyString(), anyString(), any(LimitParam.class), any(OrderParam.class));
        when(configuration.getResourceRules(anyString(), anyString())).thenReturn(ACLRule.of("alias1", "all"));

        MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();

        DefaultRestService service = new DefaultRestService(factory);
        assertEquals(service.get("alias1", "foo", "id", "1", queryParams).getStatus(), 400);
    }

    // @Test
    // public void test_get_sql_errors () throws SQLException {
    // AmforeasConfiguration confM = mock(AmforeasConfiguration.class);
    //
    // // doNothing().when(confM).load();
    // // doNothing().when(confM).loadProperties();
    // when(confM.getResourceRules(anyString(), anyString())).thenReturn(ACLRule.of("alias1", "all"));
    //
    // SingletonFactory sfM = mock(SingletonFactory.class);
    // RestController rcM = mock(RestController.class);
    // JDBCExecutor jeM = mock(JDBCExecutor.class);
    //
    // when(sfM.getConfiguration()).thenReturn(confM);
    // when(sfM.getRESTController(anyString())).thenReturn(rcM);
    // when(rcM.getResource(anyString(), anyString(), anyString(), any(LimitParam.class), any(OrderParam.class))).thenReturn(new SuccessResponse());
    // when(rcM.getExecutor()).thenReturn(jeM);
    //
    // doThrow(new SQLException()).when(jeM).get(any(Select.class), anyBoolean());
    //
    // DefaultRestService service = new DefaultRestService(sfM);
    //
    //
    // // doThrow(new IllegalArgumentException()).when(controller).getResource(anyString(), anyString(), anyString(), any(LimitParam.class),
    // // any(OrderParam.class));
    // // when(configuration.getResourceRules(anyString(), anyString())).thenReturn(ACLRule.of("alias1", "all"));
    //
    // MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    //
    // assertEquals(service.get("alias1", "foo", "id", "1", queryParams).getStatus(), 400);
    // }

}
