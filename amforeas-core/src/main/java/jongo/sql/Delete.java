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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Amforeas.  If not, see <http://www.gnu.org/licenses/>.
 */

package amforeas.sql;

/**
 * Represents a SQL DELETE statement.
 * @author Alejandro Ayuso 
 */
public class Delete {
    
    /**
     * Where the delete operation is to be performed.
     */
    private final Table table;
    
    /**
     * The value of the record to delete.
     */
    private String id;

    public Delete(Table table) {
        this.table = table;
    }
    
    public String getId() {
        return id;
    }

    public Delete setId(String id) {
        this.id = id;
        return this;
    }

    public Table getTable() {
        return table;
    }

    @Override
    public String toString() {
        return "Delete{" + "table=" + table + ", id=" + id + '}';
    }
}
