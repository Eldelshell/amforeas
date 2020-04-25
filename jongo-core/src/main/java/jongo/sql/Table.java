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

package amforeas.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a Table in a RDBMS.
 * @author Alejandro Ayuso 
 */
public class Table {
    
    /**
     * The name of a registered database or schema.
     */
    private final String database;
    
    /**
     * The name of the table or view.
     */
    private final String name;
    
    /**
     * The primary key column of the table or view. Defaults to <b>id</b>
     */
    private final String primaryKey;
    
    /**
     * A list with the columns in the table or view.
     */
    private final List<String> columns = new ArrayList<String>(); // we could extend this to include the sql.Type

    /**
     * Instantiates a new Table object for the given database and name. Sets the primaryKey property to <b>id</b>
     * @param database name of a registered database/schema
     * @param name name of a table or view
     * @throws IllegalArgumentException if the database or name are blank, null or empty.
     */
    public Table(String database, String name) {
        if(StringUtils.isBlank(database) || StringUtils.isBlank(name))
            throw new IllegalArgumentException("Argument can't be blank, null or empty");
            
        this.database = database;
        this.name = name;
        this.primaryKey = "id";
    }
    
    /**
     * Instantiates a new Table object for the given database and name with a different primary key column than <b>id</b>
     * @param database name of a registered database/schema
     * @param name name of a table or view
     * @param primaryKey the name of a column in the table or view which is to be used as primary key. If the value is
     * blank, null or empty, we default to <b>id</b>.
     * @throws IllegalArgumentException if the database or name are blank, null or empty.
     */
    public Table(String database, String name, String primaryKey) {
        if(StringUtils.isBlank(database) || StringUtils.isBlank(name))
            throw new IllegalArgumentException("Argument can't be blank, null or empty");
        
        if(StringUtils.isBlank(primaryKey))
            primaryKey = "id";
        
        this.database = database;
        this.name = name;
        this.primaryKey = primaryKey;
    }

    public List<String> getColumns() {
        return columns;
    }

    public String getDatabase() {
        return database;
    }

    public String getName() {
        return name;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Table other = (Table) obj;
        if ((this.database == null) ? (other.database != null) : !this.database.equals(other.database)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.primaryKey == null) ? (other.primaryKey != null) : !this.primaryKey.equals(other.primaryKey)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.database != null ? this.database.hashCode() : 0);
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.primaryKey != null ? this.primaryKey.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.database + "." + this.name;
    }
}
