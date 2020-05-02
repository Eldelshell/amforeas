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

package amforeas.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import amforeas.AmforeasShutdown;
import amforeas.enums.JDBCDriver;
import amforeas.exceptions.StartupException;

/**
 * Singleton class which loads the amforeas.properties files, reads its content and provides methods to access
 * this configuration properties.
 */
public class AmforeasConfiguration {

    private static final Logger l = LoggerFactory.getLogger(AmforeasConfiguration.class);

    protected final AmforeasProperties properties = new AmforeasProperties();
    protected final SystemWrapper system = new SystemWrapper();

    protected List<DatabaseConfiguration> databases = null;

    /**
     * Loads the configuration file, registers the shutdown hook, calls the generation of 
     * the database configurations and returns and instance of AmforeasConfiguration.
     * @return an instance of the AmforeasConfiguration.
     */
    public synchronized void load () {
        l.debug("Registering the shutdown hook!");
        Runtime.getRuntime().addShutdownHook(new AmforeasShutdown());

        l.debug("Loading configuration");
        try {
            this.databases = this.getDatabaseConfigurations();
        } catch (StartupException ex) {
            l.error(ex.getLocalizedMessage());
        }

        if (!this.isValid())
            throw new IllegalStateException("Configuration is not valid");
    }

    /**
     * Loads the amforeas properties from different locations using different methods.
     */
    public void loadProperties () {
        final Optional<Properties> prop = this.loadFromPath().or(this::loadFromClasspath);
        this.properties.load(prop.get());
    }

    /**
     * Loads the {@link Properties} from a given path set with -Damforeas.properties.file
     * @return Optional of {@link Properties}
     */
    private Optional<Properties> loadFromPath () {
        final String path = System.getProperty("amforeas.properties.file");

        if (StringUtils.isEmpty(path)) {
            l.debug("-Damforeas.properties.file wasn't set");
            return Optional.empty();
        }

        final File file = new File(path);

        if (!file.exists() || !file.canRead()) {
            l.error("Couldn't read -Damforeas.properties.file = {}", path);
            return Optional.empty();
        }

        final Properties prop = new Properties();

        try (FileReader reader = new FileReader(file)) {
            prop.load(new FileReader(file));
        } catch (FileNotFoundException e) {
            return Optional.empty();
        } catch (IOException e) {
            l.error("Failed to parse file {}", path);
            return Optional.empty();
        }

        return Optional.of(prop);
    }

    /**
     * Loads the {@link Properties} from the classpath
     * @return Optional of {@link Properties}
     */
    private Optional<Properties> loadFromClasspath () {
        final Properties prop = new Properties();

        try (InputStream in = AmforeasConfiguration.class.getClassLoader().getResourceAsStream("amforeas.properties")) {
            prop.load(in);
        } catch (IOException e) {
            l.error("Failed to parse file amforeas.properties");
            return Optional.empty();
        }

        return Optional.of(prop);
    }

    /**
     * From the given properties object, load the the different {@link amforeas.config.DatabaseConfiguration}.
     * @param prop an instance of {@link java.util.Properties} with the properties from the file.
     * @return a list of {@link amforeas.config.DatabaseConfiguration}
     * @throws StartupException if we're unable to load a {@link amforeas.config.DatabaseConfiguration}.
     */
    protected List<DatabaseConfiguration> getDatabaseConfigurations () throws StartupException {
        List<DatabaseConfiguration> databases = this.properties.getAliases().stream().map(this::generateDatabaseConfiguration).collect(Collectors.toList());

        if (databases.isEmpty()) {
            throw new StartupException("Failed to generate database configurations", true);
        }

        return databases;
    }

    /**
     * From the given properties object, load a {@link amforeas.config.DatabaseConfiguration}.
     * @param alias - the alias of the database to load.
     * @return a {@link amforeas.config.DatabaseConfiguration} for the alias
     */
    protected DatabaseConfiguration generateDatabaseConfiguration (final String alias) {
        l.debug("Obtain configuration options for alias {}", alias);

        JDBCDriver driver = JDBCDriver.valueOf(this.properties.get(AmforeasProperties.DB_DRIVER, alias));
        String username = this.properties.get(AmforeasProperties.DB_USERNAME, alias);
        String password = this.properties.get(AmforeasProperties.DB_PASSWORD, alias);
        String database = this.properties.get(AmforeasProperties.DB_DATABASE, alias);
        String host = this.properties.get(AmforeasProperties.DB_HOST, alias);
        Integer port = integerValueOf(AmforeasProperties.DB_PORT, alias, driver.getDefaultPort());
        Integer max = integerValueOf(AmforeasProperties.DB_MAX_CONNECTIONS, alias, Integer.valueOf(25));
        Boolean readOnly = Boolean.valueOf(this.properties.get(AmforeasProperties.DB_READONLY, alias));
        String url = this.properties.get(AmforeasProperties.DB_URL, alias);

        DatabaseConfiguration c = DatabaseConfiguration.instanceOf(alias, driver, username, password, database, host, port, max, readOnly);
        c.setUrl(url);

        l.debug("Loaded DB config {}", c.toString());
        return c;
    }

    protected Integer integerValueOf (final String field, final String alias, final Integer valueInCaseOfFailure) {
        Integer ret;
        try {
            ret = Integer.valueOf(this.properties.get(field, alias));
        } catch (Exception e) {
            ret = valueInCaseOfFailure;
        }
        return ret;
    }

    protected boolean isValid () {
        boolean ret = true;

        if (this.databases == null) {
            ret = false;
        }

        return ret;
    }

    public DatabaseConfiguration getDatabaseConfiguration (final String database) {
        return this.databases.stream().filter(conf -> conf.getDatabase().equalsIgnoreCase(database)).findFirst().orElse(null);
    }

    public DatabaseConfiguration getDatabaseConfigurationForAlias (final String alias) {
        return this.databases
            .stream()
            .filter(conf -> conf.getAlias().equalsIgnoreCase(alias))
            .findFirst()
            .orElseThrow( () -> new IllegalArgumentException("Alias doesn't exists or is not registered in amforeas"));
    }

    public List<DatabaseConfiguration> getDatabases () {
        return this.databases;
    }

    public AmforeasProperties getProperties () {
        return this.properties;
    }

    public Integer getServerPort () {
        return Integer.parseInt(this.properties.get(AmforeasProperties.SERVER_PORT));
    }

    public String getServerRoot () {
        return this.properties.get(AmforeasProperties.SERVER_ROOT);
    }

    public String getServerHost () {
        return this.properties.get(AmforeasProperties.SERVER_HOST);
    }

    public Integer getServerThreadsMin () {
        return Integer.parseInt(this.properties.get(AmforeasProperties.SERVER_THREADS_MIN));
    }

    public Integer getServerThreadsMax () {
        return Integer.parseInt(this.properties.get(AmforeasProperties.SERVER_THREADS_MAX));
    }

    public Integer getSecurePort () {
        final String port = this.properties.get(AmforeasProperties.SERVER_SECURE_PORT);

        if (StringUtils.isEmpty(port) || !StringUtils.isNumeric(port)) {
            return null;
        }

        return Integer.parseInt(port);
    }

    public String getJKSFile () {
        return this.properties.get(AmforeasProperties.SERVER_SECURE_FILE);
    }

    public String getJKSFilePassword () {
        return this.properties.get(AmforeasProperties.SERVER_SECURE_FILE_PASSWORD);
    }

}
