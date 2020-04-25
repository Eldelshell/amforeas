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

import amforeas.enums.Operator;

/**
 * Class for Select parameters.
 * @author Alejandro Ayuso
 */
public class SelectParam {
    
    /**
     * Name of the column to use in the query.
     */
    private String columnName;
    
    /**
     * {@link amforeas.enums.Operator} to use in the query.
     */
    private Operator operator;
    
    /**
     * Value the query should match.
     */
    private String value;

    public SelectParam() {}
    
    public SelectParam(String columnName, String value) {
        this.columnName = columnName;
        this.operator = Operator.EQUALS;
        this.value = value;
    }

    public SelectParam(String columnName, Operator operator, String value) {
        if(operator.isBoolean())
            throw new IllegalArgumentException("Invalid Operator " + operator.name());
        
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public SelectParam setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public Operator getOperator() {
        return operator;
    }

    public SelectParam setOperator(Operator operator) {
        if(operator.isBoolean())
            throw new IllegalArgumentException("Invalid Operator " + operator.name());
        
        this.operator = operator;
        return this;
    }

    public String getValue() {
        return value;
        
    }

    public SelectParam setValue(String value) {
        this.value = value;
        return this;
    }

    public String sql() {
        StringBuilder b = new StringBuilder(columnName).append(" ").append(operator.sql()).append(" ?");
        return b.toString();
    }
}
