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
package amforeas.sql.dialect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialect for MS SQL server.
 * @author Alejandro Ayuso 
 */
public class MSSQLDialect extends SQLDialect {
    
    private static final Logger l = LoggerFactory.getLogger(MSSQLDialect.class);

    @Override
    public String listOfTablesStatement() {
        return "select * from information_schema.tables where Table_Type = 'BASE TABLE'";
    }
    
}
