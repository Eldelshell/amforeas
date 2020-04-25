package amforeas.sql.dialect;

import amforeas.config.DatabaseConfiguration;
import amforeas.enums.JDBCDriver;

/**
 * Factory method to generate SQL Dialects
 * @author Alejandro Ayuso 
 */
public class DialectFactory {
    
    /**
     * Obtain a dialect for the given {@link amforeas.config.DatabaseConfiguration}.
     * @param dbconf a {@link amforeas.config.DatabaseConfiguration}.
     * @return a {@link amforeas.sql.dialect.Dialect} for the driver.
     */
    public static Dialect getDialect(final DatabaseConfiguration dbconf){
        return getDialect(dbconf.getDriver());
    }
    
    /**
     * For a given driver, return the appropriate dialect.
     * @param driver a {@link amforeas.enums.JDBCDriver}
     * @return a {@link amforeas.sql.dialect.Dialect} for the driver.
     */
    public static Dialect getDialect(final JDBCDriver driver){
        Dialect dialect; 
        switch(driver){
            case HSQLDB_MEM:
            case HSQLDB_FILE:
                dialect = new HSQLDialect(); 
                break;
            
            case MSSQL_JTDS:
            case MSSQL: 
                dialect = new MSSQLDialect(); 
                break;
                
            case H2_MEM:
            case H2_FILE:
            case H2_REMOTE:
            	dialect = new H2Dialect();
            	break;
            
            case MySQL: dialect = new MySQLDialect(); break;
            case ORACLE: dialect = new OracleDialect(); break;
            case PostgreSQL: dialect = new PostgreSQLDialect(); break;
            case DERBY_MEM: dialect = new DerbyDialect(); break;
            default: dialect = new SQLDialect(); break;
        }
        return dialect;
        
    }
}
