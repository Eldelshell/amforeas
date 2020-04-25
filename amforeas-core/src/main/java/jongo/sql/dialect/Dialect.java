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

import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;
import amforeas.sql.Delete;
import amforeas.sql.DynamicFinder;
import amforeas.sql.Insert;
import amforeas.sql.Select;
import amforeas.sql.Update;

/**
 * Interface which describes Dialect implementations.
 * @version 1
 * @author Alejandro Ayuso 
 */
public interface Dialect {
    
    /**
     * Generate the appropriate SQL statement for a {@link amforeas.sql.Insert} instance.
     * @param insert a {@link amforeas.sql.Insert} instance.
     * @return a SQL statement representation of the {@link amforeas.sql.Insert} instance.
     */
    public String toStatementString(final Insert insert); // C
    
    /**
     * Generate the appropriate SQL statement for a {@link amforeas.sql.Select} instance.
     * @param select a {@link amforeas.sql.Select} instance.
     * @return a SQL statement representation of the {@link amforeas.sql.Select} instance.
     */
    public String toStatementString(final Select select); // R
    
    /**
     * Generate the appropriate SQL statement for a {@link amforeas.sql.Update} instance.
     * @param update a {@link amforeas.sql.Update} instance.
     * @return a SQL statement representation of the {@link amforeas.sql.Update} instance.
     */
    public String toStatementString(final Update update); // U
    
    /**
     * Generate the appropriate SQL statement for a {@link amforeas.sql.Delete} instance.
     * @param delete a {@link amforeas.sql.Delete} instance.
     * @return a SQL statement representation of the {@link amforeas.sql.Delete} instance.
     */
    public String toStatementString(final Delete delete); // D
    
    /**
     * Generate the appropriate SQL statement for a {@link org.amforeas.jdbc.DynamicFinder} instance.
     * @param finder a {@link org.amforeas.jdbc.DynamicFinder} instance.
     * @param limit a {@link amforeas.jdbc.LimitParam} instance.
     * @param order a {@link amforeas.jdbc.OrderParam} instance.
     * @return a SQL statement representation of the {@link org.amforeas.jdbc.DynamicFinder} instance.
     */
    public String toStatementString(final DynamicFinder finder, final LimitParam limit, final OrderParam order);
    
    /**
     * Return a SQL statement used to obtain a list of tables for a given database or schema.
     * @return a statement used to query your RDBMS for the list of tables.
     */
    public String listOfTablesStatement();
}