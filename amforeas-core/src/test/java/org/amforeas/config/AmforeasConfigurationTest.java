package org.amforeas.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.config.AmforeasConfiguration;

@Tag("offline-tests")
class AmforeasConfigurationTest {

    @Test
    void test_formatProperty () {
        AmforeasConfiguration conf = AmforeasConfiguration.instanceOf();
        assertEquals("PROPERTY_NAME", conf.formatProperty("property.name"));

        // This should fail when we set $export AMFOREAS_SERVER_PORT=808
        assertEquals(8080, conf.getServerPort());
    }

}
