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

package amforeas;

import amforeas.config.AmforeasConfiguration;
import amforeas.jdbc.JDBCConnectionFactory;
import amforeas.jdbc.JDBCExecutor;
import amforeas.sql.dialect.DialectFactory;

public class SingletonFactoryImpl implements SingletonFactory {

    protected static JDBCExecutor jdbcExecutor;
    protected static AmforeasConfiguration configuration;
    protected static JDBCConnectionFactory jdbcConnectionFactory;
    protected static DialectFactory dialectFactory;

    public synchronized JDBCExecutor getJDBCExecutor () {
        if (jdbcExecutor == null) {
            jdbcExecutor = new JDBCExecutor();
        }
        return jdbcExecutor;
    }

    public synchronized AmforeasConfiguration getConfiguration () {
        if (configuration == null) {
            configuration = new AmforeasConfiguration();
            configuration.loadProperties();
            configuration.load();
        }
        return configuration;
    }

    public synchronized void resetConfiguration () {
        configuration = null;
    }

    public synchronized JDBCConnectionFactory getJDBCConnectionFactory () {
        if (jdbcConnectionFactory == null) {
            jdbcConnectionFactory = new JDBCConnectionFactory();
            jdbcConnectionFactory.load();
        }
        return jdbcConnectionFactory;
    }

    public synchronized RestController getRESTController (String alias) {
        return new RestController(alias);
    }

    public DialectFactory getDialectFactory () {
        if (dialectFactory == null) {
            dialectFactory = new DialectFactory();
        }
        return dialectFactory;
    }

}
