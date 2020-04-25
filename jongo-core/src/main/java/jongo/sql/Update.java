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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import amforeas.enums.Operator;

/**
 * Represents a SQL UPDATE statement.
 * @author Alejandro Ayuso 
 */
public class Update {
    
    /**
     * Where the insert operation is to be performed.
     */
    private final Table table;
    
    /**
     * The value of the record to update.
     */
    private String id;
    
    /**
     * Values to be updated.
     */
    private Map<String, String> columns = new LinkedHashMap<String, String>();

    public Update(Table table) {
        this.table = table;
    }
    
    public Update addColumn(String columnName, String value) {
        columns.put(columnName, value);
		return this;
	}
    
    public Map<String, String> getColumns() {
        return columns;
    }
    
    public Update setColumns(Map<String, String> columns){
        this.columns = columns;
        return this;
    }

    public String getId() {
        return id;
    }

    public Update setId(String id) {
        this.id = id;
        return this;
    }

    public Table getTable() {
        return table;
    }
    
    public Select getSelect(){
        return new Select(table).setParameter(new SelectParam(table.getPrimaryKey(), Operator.EQUALS, id));
    }
    
    public List<String> getParameters(){
        List<String> params = new ArrayList<String>(this.columns.values());
        params.add(id);
        return params;
    }

    @Override
    public String toString() {
        return "Update{" + "table=" + table + ", id=" + id + ", columns=" + columns + '}';
    }
}
