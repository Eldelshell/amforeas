package amforeas;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application entry point for jersey. Scan packages for discovery.
 */
@ApplicationPath("/")
public class App extends ResourceConfig {

    private static final Logger l = LoggerFactory.getLogger(App.class);

    public App() {
        l.info("Scanning Jersey App");
        packages("amforeas.rest");
    }

}
