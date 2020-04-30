package amforeas.demo;

import amforeas.SingletonFactory;
import amforeas.config.AmforeasConfiguration;

public class DemoSingletonFactory extends SingletonFactory {

    public synchronized AmforeasConfiguration getConfiguration () {
        if (configuration == null) {
            configuration = new DemoConfiguration();
            configuration.load();
        }
        return configuration;
    }

}
