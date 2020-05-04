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

package amforeas.sql.dialect;

import amforeas.config.DatabaseConfiguration;
import amforeas.enums.JDBCDriver;

/**
 * Factory method to generate SQL Dialects
 */
public class DialectFactory {

    /**
     * Obtain a dialect for the given {@link amforeas.config.DatabaseConfiguration}.
     * @param dbconf a {@link amforeas.config.DatabaseConfiguration}.
     * @return a {@link amforeas.sql.dialect.Dialect} for the driver.
     */
    public Dialect getDialect (final DatabaseConfiguration dbconf) {
        return getDialect(dbconf.getDriver());
    }

    /**
     * For a given driver, return the appropriate dialect.
     * @param driver a {@link amforeas.enums.JDBCDriver}
     * @return a {@link amforeas.sql.dialect.Dialect} for the driver.
     */
    public Dialect getDialect (final JDBCDriver driver) {
        Dialect dialect;
        switch (driver) {
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

            case MySQL:
                dialect = new MySQLDialect();
                break;
            case ORACLE:
                dialect = new OracleDialect();
                break;
            case PostgreSQL:
                dialect = new PostgreSQLDialect();
                break;
            case DERBY_MEM:
                dialect = new DerbyDialect();
                break;
            default:
                dialect = new SQLDialect();
                break;
        }
        return dialect;

    }
}
