package amforeas.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import amforeas.AmforeasShutdown;
import amforeas.config.AmforeasConfiguration;

public class DemoConfiguration extends AmforeasConfiguration {

    private static final Logger l = LoggerFactory.getLogger(DemoConfiguration.class);

    public DemoConfiguration() {
        super();
    }

    public synchronized void load () {
        l.debug("Registering the shutdown hook!");
        Runtime.getRuntime().addShutdownHook(new AmforeasShutdown());

        l.debug("Loading demo configuration with memory databases");
        this.databases = Demo.getDemoDatabasesConfiguration();
        Demo.generateDemoDatabases(this.getDatabases());

        if (!this.isValid())
            throw new IllegalStateException("Configuration is not valid");
    }

}
