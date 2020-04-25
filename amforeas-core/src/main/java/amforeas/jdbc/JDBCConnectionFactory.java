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

package amforeas.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;

import amforeas.config.DatabaseConfiguration;
import amforeas.config.AmforeasConfiguration;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton in charge of registering a pool of connections for each database 
 * and provide access to this connections.
 * @author Alejandro Ayuso 
 */
public class JDBCConnectionFactory {

    private static final Logger l = LoggerFactory.getLogger(JDBCConnectionFactory.class);
    private static final AmforeasConfiguration configuration = AmforeasConfiguration.instanceOf();
    
    private final Map<String, GenericObjectPool> connectionPool = new ConcurrentHashMap<String,GenericObjectPool>();
    
    private static JDBCConnectionFactory instance = null;
    
    private JDBCConnectionFactory(){}
    
    /**
     * Instantiates a new JDBCConnectionFactory if required and creates a connections pool for every database.
     * @return the instance of the singleton.
     */
    private static JDBCConnectionFactory instanceOf(){
        if(instance == null){
            instance = new JDBCConnectionFactory();
            for(DatabaseConfiguration db : configuration.getDatabases()){
                l.debug("Registering Connection Pool for {}", db.getDatabase());
                GenericObjectPool pool = new GenericObjectPool(null, db.getMaxConnections());
                ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(db.toJdbcURL(), db.getUsername(), db.getPassword());
                PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, pool, null, null, db.isReadOnly(), true);
                poolableConnectionFactory.hashCode();
                instance.connectionPool.put(db.getDatabase(), pool);
            }
        }
        return instance;
    }

    /**
     * Gives access to a {@link java.sql.Connection} for the given database.
     * @param dbcfg a registered {@link amforeas.config.DatabaseConfiguration}
     * @return a {@link java.sql.Connection}
     * @throws SQLException 
     */
    public static Connection getConnection(final DatabaseConfiguration dbcfg) throws SQLException {
        l.debug("Obtaining a connection from the datasource");
        DataSource ds = getDataSource(dbcfg);
        return ds.getConnection();
    }

    /**
     * Gives access to a {@link java.sql.DataSource} for the given database.
     * @param dbcfg a registered {@link amforeas.config.DatabaseConfiguration}
     * @return a {@link java.sql.DataSource}
     */
    public static DataSource getDataSource(final DatabaseConfiguration dbcfg) {
        JDBCConnectionFactory me = JDBCConnectionFactory.instanceOf();
        PoolingDataSource dataSource = new PoolingDataSource(me.connectionPool.get(dbcfg.getDatabase()));
        return dataSource;
    }

    /**
     * Instantiates a new {@linkplain org.apache.commons.dbutils.QueryRunner} for the given database.
     * @param dbcfg a registered {@link amforeas.config.DatabaseConfiguration}
     * @return a new {@linkplain org.apache.commons.dbutils.QueryRunner}
     */
    public static QueryRunner getQueryRunner(final DatabaseConfiguration dbcfg){
        DataSource ds = getDataSource(dbcfg);
        return new QueryRunner(ds);
    }
    
    /**
     * Close all connections in the pool.
     * @throws SQLException 
     */
    public static void closeConnections() throws SQLException{
        for(DatabaseConfiguration dbcfg : configuration.getDatabases()){
            l.debug("Shutting down JDBC connection {}", dbcfg.getDatabase());
            getConnection(dbcfg).close();
        }
    }
}
