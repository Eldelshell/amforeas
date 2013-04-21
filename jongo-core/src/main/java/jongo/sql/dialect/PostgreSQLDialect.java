/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
 *
 * This file is part of Jongo.
 * Jongo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * Jongo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jongo.  If not, see <http://www.gnu.org/licenses/>.
 */
package jongo.sql.dialect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialect for PostgreSQL.
 * @author Alejandro Ayuso 
 */
public class PostgreSQLDialect extends SQLDialect {
    
    private static final Logger l = LoggerFactory.getLogger(PostgreSQLDialect.class);

    @Override
    public String listOfTablesStatement() {
        return "SELECT * FROM information_schema.tables WHERE table_schema = 'public'";
    }
    
}
