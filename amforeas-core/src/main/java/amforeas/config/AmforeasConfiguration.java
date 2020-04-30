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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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

    protected static final String p_prefix = "amforeas.";

    /* Server */
    protected static final String server_root = p_prefix + "server.root";
    protected static final String server_host = p_prefix + "server.host";
    protected static final String server_port = p_prefix + "server.http.port";

    protected static final String server_threads_min = p_prefix + "server.threads.min";
    protected static final String server_threads_max = p_prefix + "server.threads.max";

    /* SSL */
    protected static final String server_secure_port = p_prefix + "server.https.port";
    protected static final String server_secure_file = p_prefix + "server.https.jks";
    protected static final String server_secure_file_password = p_prefix + "server.https.jks.password";

    /* Database */

    protected static final String p_name_amforeas_database_list = p_prefix + "alias.list";
    protected static final String p_prefix_db_driver = ".jdbc.driver";
    protected static final String p_prefix_db_username = ".jdbc.username";
    protected static final String p_prefix_db_password = ".jdbc.password";
    protected static final String p_prefix_db_database = ".jdbc.database";
    protected static final String p_prefix_db_host = ".jdbc.host";
    protected static final String p_prefix_db_port = ".jdbc.port";
    protected static final String p_prefix_db_readonly = ".jdbc.readonly";
    protected static final String p_prefix_db_max_connections = ".jdbc.max.connections";
    protected static final String p_prefix_db_url = ".jdbc.url";

    protected Integer limit;
    protected Integer maxLimit;
    protected boolean listTables;
    protected Properties properties;

    protected List<DatabaseConfiguration> databases = null;

    public AmforeasConfiguration() {
        this.properties = this.loadProperties();
    }

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
     * Loads the amforeas.properties from different locations using different methods.
     * @param conf a AmforeasConfiguration instance used to obtain a ClassLoader.
     * @return an instance of {@link java.util.Properties} with the properties from the file.
     */
    protected Properties loadProperties () {
        final Properties prop = new Properties();
        InputStream in = AmforeasConfiguration.class.getClass().getResourceAsStream("/org/amforeas/amforeas.properties");

        if (in == null) {
            l.warn("Couldn't load configuration file /org/amforeas/amforeas.properties");
            in = AmforeasConfiguration.class.getClass().getResourceAsStream("/amforeas.properties");
        }

        if (in == null) {
            l.warn("Couldn't load configuration file /amforeas.properties");
            in = AmforeasConfiguration.class.getClassLoader().getResourceAsStream("amforeas.properties");
        }

        if (in == null) {
            l.error("Couldn't load configuration file amforeas.properties quitting");
        }

        try {
            if (in != null) {
                prop.load(in);
            }
        } catch (IOException ex) {
            l.error("Failed to load configuration", ex);
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException ex) {
                l.error(ex.getMessage());
            }
        }
        return prop;
    }

    /**
     * From the given properties object, load the the different {@link amforeas.config.DatabaseConfiguration}.
     * @param prop an instance of {@link java.util.Properties} with the properties from the file.
     * @return a list of {@link amforeas.config.DatabaseConfiguration}
     * @throws StartupException if we're unable to load a {@link amforeas.config.DatabaseConfiguration}.
     */
    protected List<DatabaseConfiguration> getDatabaseConfigurations () throws StartupException {
        String databaseList = this.properties.getProperty(p_name_amforeas_database_list);
        if (databaseList == null) {
            throw new StartupException("Failed to read list of aliases " + p_name_amforeas_database_list, true);
        }
        final String[] names = databaseList.split(",");
        List<DatabaseConfiguration> databases = new ArrayList<DatabaseConfiguration>(names.length);
        for (String name : names) {
            name = name.trim();
            if (StringUtils.isAlphanumeric(name)) {
                DatabaseConfiguration c = generateDatabaseConfiguration(name);
                databases.add(c);
            } else {
                l.warn("Database name {} is invalid. Continuing without it.", name);
            }
        }
        return databases;
    }

    /**
     * From the given properties object, load a {@link amforeas.config.DatabaseConfiguration}.
     * @param prop an instance of {@link java.util.Properties} with the properties from the file.
     * @param name the name of the database to load.
     * @return a {@link amforeas.config.DatabaseConfiguration}for the name given to the
     * database/schema.
     */
    protected DatabaseConfiguration generateDatabaseConfiguration (final String name) {
        l.debug("Obtain configuration options for alias {}", name);

        JDBCDriver driver = JDBCDriver.valueOf(this.properties.getProperty(p_prefix + name + p_prefix_db_driver));
        String username = this.properties.getProperty(p_prefix + name + p_prefix_db_username);
        String password = this.properties.getProperty(p_prefix + name + p_prefix_db_password);
        String database = this.properties.getProperty(p_prefix + name + p_prefix_db_database);
        String host = this.properties.getProperty(p_prefix + name + p_prefix_db_host);
        Integer port = integerValueOf(this.properties, p_prefix + name + p_prefix_db_port, driver.getDefaultPort());
        Integer max = integerValueOf(this.properties, p_prefix + name + p_prefix_db_max_connections, Integer.valueOf(25));
        Boolean readOnly = Boolean.valueOf(this.properties.getProperty(p_prefix + name + p_prefix_db_readonly));
        String url = this.properties.getProperty(p_prefix + name + p_prefix_db_url);

        DatabaseConfiguration c = DatabaseConfiguration.instanceOf(name, driver, username, password, database, host, port, max, readOnly);
        c.setUrl(url);

        l.debug("Loaded DB config {}", c.toString());
        return c;
    }

    protected static Integer integerValueOf (final Properties prop, final String field, final Integer valueInCaseOfFailure) {
        Integer ret;
        try {
            ret = Integer.valueOf(prop.getProperty(field));
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

    public JDBCDriver getDriver (final String database) {
        for (DatabaseConfiguration c : this.databases) {
            if (c.getDatabase().equalsIgnoreCase(database))
                return c.getDriver();
        }
        DatabaseConfiguration c = getDatabaseConfiguration(database);
        return c != null ? c.getDriver() : null;
    }

    public Integer getLimit () {
        return limit;
    }

    public Integer getMaxLimit () {
        return maxLimit;
    }

    public DatabaseConfiguration getDatabaseConfiguration (final String database) {
        for (DatabaseConfiguration c : this.databases) {
            if (c.getDatabase().equalsIgnoreCase(database))
                return c;
        }
        return null;
    }

    public DatabaseConfiguration getDatabaseConfigurationForAlias (final String alias) {
        for (DatabaseConfiguration c : this.databases) {
            if (c.getAlias().equalsIgnoreCase(alias))
                return c;
        }
        throw new IllegalArgumentException("Alias doesn't exists or is not registered in amforeas");
    }

    public List<DatabaseConfiguration> getDatabases () {
        return this.databases;
    }

    public boolean allowListTables () {
        return listTables;
    }

    public Properties getProperties () {
        return this.properties;
    }

    public String formatProperty (final String property) {
        return property.replace(".", "_").replace("-", "_").toUpperCase();
    }

    public String getProperty (final String name) {
        return this.getProperty(name, null);
    }

    public String getProperty (final String name, final String defVal) {
        final String env = System.getenv(this.formatProperty(name));
        if (StringUtils.isNotEmpty(env)) {
            return env;
        }

        return this.getProperties().getProperty(name, defVal);
    }

    public Integer getServerPort () {
        return Integer.parseInt(this.getProperty(server_port));
    }

    public String getServerRoot () {
        return this.getProperty(server_root);
    }

    public String getServerHost () {
        return this.getProperty(server_host);
    }

    public Integer getServerThreadsMin () {
        return Integer.parseInt(this.getProperty(server_threads_min));
    }

    public Integer getServerThreadsMax () {
        return Integer.parseInt(this.getProperty(server_threads_max));
    }

    public Integer getSecurePort () {
        return Integer.parseInt(this.getProperty(server_secure_port, "0"));
    }

    public String getJKSFile () {
        return this.getProperty(server_secure_file);
    }

    public String getJKSFilePassword () {
        return this.getProperty(server_secure_file_password);
    }
}
