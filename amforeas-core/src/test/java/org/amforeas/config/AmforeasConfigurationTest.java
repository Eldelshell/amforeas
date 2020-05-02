package org.amforeas.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
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

    @Mock
    SingletonFactory factory;

    @Test
    void test_formatProperty () {
        // This way we stop the configuration from setting up the database
        when(factory.getConfiguration()).thenReturn(new AmforeasConfiguration());

        AmforeasConfiguration conf = factory.getConfiguration();
        assertEquals("PROPERTY_NAME", conf.formatProperty("property.name"));

        // This should fail when we set $export AMFOREAS_SERVER_PORT=808 or -Damforeas.server.port=808
        assertEquals(8080, conf.getServerPort());
    }

}
