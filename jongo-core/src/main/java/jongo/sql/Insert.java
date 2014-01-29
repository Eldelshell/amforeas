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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jongo.  If not, see <http://www.gnu.org/licenses/>.
 */

package jongo.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a SQL INSERT statement.
 * @author Alejandro Ayuso 
 */
public class Insert {
    
    /**
     * Where the insert operation is to be performed.
     */
	private final Table table;
    
    /**
     * Values to be inserted.
     */
	private Map<String, String> columns = new LinkedHashMap<String, String>();

    public Insert(Table table) {
        this.table = table;
    }
    
	public Insert addColumn(String columnName, String value) {
        if(StringUtils.isNotEmpty(value))
            columns.put(columnName, value);
		return this;
	}

    public Map<String, String> getColumns() {
        return columns;
    }

    public Table getTable() {
        return table;
    }

    public Insert setColumns(Map<String, String> columns) {
        this.columns = columns;
        return this;
    }
    
    public List<String> getValues(){
        return new ArrayList<String>(this.columns.values());
    }

    @Override
    public String toString() {
        return "Insert{" + "table=" + table + ", columns=" + columns + '}';
    }
}
