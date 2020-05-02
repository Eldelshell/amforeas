package org.amforeas.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import amforeas.SingletonFactory;
import amforeas.config.AmforeasConfiguration;

@ExtendWith(MockitoExtension.class)
@Tag("offline-tests")
class AmforeasConfigurationTest {

    private final Properties javaProperties = new Properties();

    private AmforeasConfiguration conf;

    @Mock
    SingletonFactory factory;

    @BeforeEach
    public void setUpEach () {
        javaProperties.clear();
        javaProperties.setProperty("amforeas.server.root", "/amforeas/*");
        javaProperties.setProperty("amforeas.server.host", "0.0.0.0");
        javaProperties.setProperty("amforeas.server.http.port", "8080");
        javaProperties.setProperty("amforeas.alias.list", "alias1, alias2");
        javaProperties.setProperty("amforeas.alias1.jdbc.driver", "H2_MEM");
        javaProperties.setProperty("amforeas.alias1.jdbc.database", "test_db");
        javaProperties.setProperty("amforeas.alias2.jdbc.driver", "MSSQL_JTDS");
        javaProperties.setProperty("amforeas.alias2.jdbc.database", "test_db2");

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

    private class AmforeasConfigurationStub extends AmforeasConfiguration {

        public void loadProperties () {
            this.properties.load(javaProperties);
        }

    }

}
