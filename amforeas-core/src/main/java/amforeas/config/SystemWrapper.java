package amforeas.config;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for java.lang.System so we can mock it.
 */
public class SystemWrapper {

    private static final Logger l = LoggerFactory.getLogger(SystemWrapper.class);

    /**
     * Converts a property name my.property to MY_PROPERTY
     * @param property - the name of the property
     * @return the formatted property parameter
     */
    public static String formatProperty (final String property) {
        return property.replace(".", "_").replace("-", "_").toUpperCase();
    }

    /**
     * Checks if a property has been set as an environment variable or as a VM argument.
     * @param name - the property to retrieve
     * @return Optional with the value or empty.
     */
    public Optional<String> get (final String name) {
        final String env = System.getenv(formatProperty(name));
        if (StringUtils.isNotEmpty(env)) {
            l.warn("Overriding property {} with environment variable {}", name, env);
            return Optional.of(env);
        }

        final String property = System.getProperty(name);
        if (StringUtils.isNotEmpty(property)) {
            l.warn("Overriding property {} with variable -D{}", name, name);
            return Optional.of(property);
        }

        // l.debug("No system value for {}", name);
        return Optional.empty();
    }

}
