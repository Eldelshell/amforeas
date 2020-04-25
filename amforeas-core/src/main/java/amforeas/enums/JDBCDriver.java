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

package amforeas.enums;

/**
 * An enum with the different supported JDBC drivers and its class name used for loading.
 * @author Alejandro Ayuso 
 */
public enum JDBCDriver {
    HSQLDB_MEM      ("org.hsqldb.jdbcDriver",                           Integer.valueOf(0)),
    HSQLDB_FILE     ("org.hsqldb.jdbcDriver",                           Integer.valueOf(0)),
    MySQL           ("com.mysql.jdbc.Driver",                           Integer.valueOf(3306)),
    PostgreSQL      ("org.postgresql.Driver",                           Integer.valueOf(5432)),
    ORACLE          ("oracle.jdbc.driver.OracleDriver",                 Integer.valueOf(1521)),
    MSSQL_JTDS      ("net.sourceforge.jtds.jdbc.Driver",                Integer.valueOf(1433)),
    MSSQL           ("com.microsoft.jdbc.sqlserver.SQLServerDriver",    Integer.valueOf(1433)),
    H2_MEM          ("org.h2.Driver",                                   Integer.valueOf(0)),
    H2_FILE         ("org.h2.Driver",                                   Integer.valueOf(0)),
    H2_REMOTE       ("org.h2.Driver",                                   Integer.valueOf(0)),
    DERBY_MEM		("org.apache.derby.jdbc.EmbeddedDriver",			Integer.valueOf(0));
    
    private final String name;
    private final Integer port;
    
    /**
     * Constructor which builds an enum with the default driver class string
     * and the default port.
     * @param driverName class string name of the driver
     * @param port default port used by the driver
     */
    private JDBCDriver(final String driverName, final Integer port){
    	this.name = driverName;
        this.port = port;
    }
    
    /**
     * From a given driver class name check if we currently support it.
     * @param driverName the name of the driver class.
     * @return true if the driver is supported.
     */
    public static boolean supported(final String driverName){
        for(JDBCDriver driver : JDBCDriver.values()){
            if(driver.name.equals(driverName)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * From a given driver class name, return its JDBCDriver representation.
     * @param driverName the name of the driver class.
     * @return a JDBCDriver for the given drivername
     * @throws IllegalArgumentException if the given driver name is not supported or null.
     */
    public static JDBCDriver driverOf(final String driverName){
        if(driverName == null)
            throw new IllegalArgumentException("Provide a driver");
        
        for(JDBCDriver driver : JDBCDriver.values()){
            if(driver.name.equals(driverName)){
                return driver;
            }
        }

        throw new IllegalArgumentException(driverName + " not supported");
    }
    
    public String getName(){
        return this.name;
    }

    public Integer getDefaultPort() {
        return port;
    }
}
