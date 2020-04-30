package amforeas;

import amforeas.config.AmforeasConfiguration;
import amforeas.jdbc.JDBCConnectionFactory;
import amforeas.jdbc.JDBCExecutor;

public class SingletonFactory {

    private static JDBCExecutor jdbcExecutor;
    private static AmforeasConfiguration configuration;
    private static JDBCConnectionFactory jdbcConnectionFactory;

    public synchronized JDBCExecutor getJDBCExecutor () {
        if (jdbcExecutor == null) {
            jdbcExecutor = new JDBCExecutor();
        }
        return jdbcExecutor;
    }

    public synchronized AmforeasConfiguration getConfiguration () {
        if (configuration == null) {
            configuration = new AmforeasConfiguration();
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

}
