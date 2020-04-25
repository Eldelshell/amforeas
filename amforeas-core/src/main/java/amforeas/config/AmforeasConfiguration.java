/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
 *
 * This file is part of Amforeas.
 * Amforeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * Amforeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Amforeas.  If not, see <http://www.gnu.org/licenses/>.
 */

package amforeas.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import amforeas.AmforeasShutdown;
import amforeas.demo.Demo;
import amforeas.enums.JDBCDriver;
import amforeas.exceptions.StartupException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton class which loads the amforeas.properties files, reads its content and provides methods to access
 * this configuration properties.
 * @author Alejandro Ayuso 
 */
public class AmforeasConfiguration {
    
    private static final Logger l = LoggerFactory.getLogger(AmforeasConfiguration.class);
    
    private static final String p_name_jongo_database_list = "amforeas.alias.list";
    private static final String p_prefix_db_driver = ".jdbc.driver";
    private static final String p_prefix_db_username = ".jdbc.username";
    private static final String p_prefix_db_password = ".jdbc.password";
    private static final String p_prefix_db_database = ".jdbc.database";
    private static final String p_prefix_db_host = ".jdbc.host";
    private static final String p_prefix_db_port = ".jdbc.port";
    private static final String p_prefix_db_readonly = ".jdbc.readonly";
    private static final String p_prefix_db_max_connections = ".jdbc.max.connections";
    private static final String p_prefix_db_url = ".jdbc.url";
    
    private static AmforeasConfiguration instance;
    
    private Integer limit;
    private Integer maxLimit;
    private boolean listTables;
    
    private List<DatabaseConfiguration> databases = null;
    
    private static final boolean demo = (System.getProperty("environment") != null && System.getProperty("environment").equalsIgnoreCase("demo")); 
    
    private AmforeasConfiguration(){}
    
    /**
     * Loads the configuration file, registers the shutdown hook, calls the generation of 
     * the database configurations and returns and instance of AmforeasConfiguration.
     * @return an instance of the AmforeasConfiguration.
     */
    public synchronized static AmforeasConfiguration instanceOf(){
        if(instance == null){
            instance = new AmforeasConfiguration();
            Properties prop = getProperties(instance);
            
            l.debug("Registering the shutdown hook!");
            Runtime.getRuntime().addShutdownHook(new AmforeasShutdown());
            
            if(demo){
                l.debug("Loading demo configuration with memory databases");
                instance.databases = Demo.getDemoDatabasesConfiguration();
                Demo.generateDemoDatabases(instance.getDatabases());
            }else{
                l.debug("Loading configuration");
                try {
                    instance.databases = getDatabaseConfigurations(prop);
                } catch (StartupException ex) {
                    l.error(ex.getLocalizedMessage());
                }
            }
            
            if(!instance.isValid()) instance = null;
            
        }
        return instance;
    }
    
    public static void reset(){
        instance = null;
    }
    
    private static Properties getProperties(AmforeasConfiguration conf){
        Properties prop;
        if(demo){
            prop = loadDemoProperties();
        }else{
            prop = loadProperties(conf);
        }
        return prop;
    }
    
    private static Properties loadDemoProperties(){
        return new Properties();
    }
    
    /**
     * Loads the amforeas.properties from different locations using different methods.
     * @param conf a AmforeasConfiguration instance used to obtain a ClassLoader.
     * @return an instance of {@link java.util.Properties} with the properties from the file.
     */
    private static Properties loadProperties(AmforeasConfiguration conf){
        Properties prop = new Properties();
        InputStream in = AmforeasConfiguration.class.getClass().getResourceAsStream("/org/amforeas/amforeas.properties");

        if(in == null){
            l.warn("Couldn't load configuration file /org/amforeas/amforeas.properties");
            in = AmforeasConfiguration.class.getClass().getResourceAsStream("/amforeas.properties");
        }
        
        if(in == null){
            l.error("Couldn't load configuration file /amforeas.properties");
            in = conf.getClass().getClassLoader().getResourceAsStream("amforeas.properties");
        }
        
        if(in == null){
            l.error("Couldn't load configuration file amforeas.properties quitting");
        }

        try {
            if(in != null){
                prop.load(in);
            }
        } catch (IOException ex) {
            l.error("Failed to load configuration", ex);
        }finally{
            try {
                if(in != null) in.close();
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
    private static List<DatabaseConfiguration> getDatabaseConfigurations(final Properties prop) throws StartupException{
        String databaseList = prop.getProperty(p_name_jongo_database_list);
        if(databaseList == null){
            throw new StartupException("Failed to read list of aliases " + p_name_jongo_database_list, demo);
        }
        final String [] names = databaseList.split(",");
        List<DatabaseConfiguration> databases = new ArrayList<DatabaseConfiguration>(names.length);
        for(String name : names){
            name = name.trim();
            if(StringUtils.isAlphanumeric(name)){
                DatabaseConfiguration c = generateDatabaseConfiguration(prop, name);
                databases.add(c);
            }else{
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
    private static DatabaseConfiguration generateDatabaseConfiguration(final Properties prop, final String name){
        l.debug("Obtain configuration options for alias {}", name);
        
        JDBCDriver driver = JDBCDriver.valueOf(prop.getProperty(name + p_prefix_db_driver));
        String username =   prop.getProperty(name + p_prefix_db_username);
        String password =   prop.getProperty(name + p_prefix_db_password);
        String database =   prop.getProperty(name + p_prefix_db_database);
        String host =       prop.getProperty(name + p_prefix_db_host);
        Integer port =      integerValueOf(prop, name + p_prefix_db_port, driver.getDefaultPort());
        Integer max =       integerValueOf(prop, name + p_prefix_db_max_connections, Integer.valueOf(25));
        Boolean readOnly =  Boolean.valueOf(prop.getProperty(name + p_prefix_db_readonly));
        String url  =       prop.getProperty(name+p_prefix_db_url);
        DatabaseConfiguration c = DatabaseConfiguration.instanceOf(name, driver, username, password, database, host, port, max, readOnly);
        c.setUrl(url);
        l.debug("Loaded DB config {}", c.toString());
        return c;
    }
    
    private static Integer integerValueOf(final Properties prop, final String field, final Integer valueInCaseOfFailure){
        Integer ret;
        try{
            ret = Integer.valueOf(prop.getProperty(field));
        }catch(Exception e){
            ret = valueInCaseOfFailure;
        }
        return ret;
    }
    
    private boolean isValid(){
        boolean ret = true;
        
        if(instance.databases == null){
            ret = false;
        }
        
        return ret;
    }

    public JDBCDriver getDriver(final String database) {
        for(DatabaseConfiguration c : this.databases){
            if(c.getDatabase().equalsIgnoreCase(database))
                return c.getDriver();
        }
        DatabaseConfiguration c = getDatabaseConfiguration(database);
        return c != null ? c.getDriver() : null; 
    }

    public boolean isDemoModeActive(){
        return demo;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getMaxLimit() {
        return maxLimit;
    }
    
    public DatabaseConfiguration getDatabaseConfiguration(final String database){
        for(DatabaseConfiguration c : this.databases){
            if(c.getDatabase().equalsIgnoreCase(database))
                return c;
        }
        return null;
    }
    
    public DatabaseConfiguration getDatabaseConfigurationForAlias(final String alias){
        for(DatabaseConfiguration c : this.databases){
            if(c.getAlias().equalsIgnoreCase(alias))
                return c;
        }
        throw new IllegalArgumentException("Alias doesn't exists or is not registered in amforeas");
    }
    
    public List<DatabaseConfiguration> getDatabases(){
        return this.databases;
    }

    public boolean allowListTables() {
        return listTables;
    }
}
