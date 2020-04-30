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

package amforeas.sql;

import org.apache.commons.lang3.StringUtils;
import amforeas.enums.Operator;

/**
 * Class for Select parameters.
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
    private String[] values;

    public SelectParam(String columnName, String value) {
        if (StringUtils.isBlank(columnName) || StringUtils.isBlank(value))
            throw new IllegalArgumentException("Argument can't be blank, null or empty");

        this.columnName = columnName;
        this.operator = Operator.EQUALS;
        this.values = new String[] {value};
    }

    public SelectParam(String columnName, Operator operator, String... values) {
        if (StringUtils.isBlank(columnName))
            throw new IllegalArgumentException("Argument columnName can't be blank, null or empty");

        if (operator == null)
            throw new IllegalArgumentException("Argument operator can't be blank, null or empty");

        if (operator.isBoolean())
            throw new IllegalArgumentException("Invalid Operator " + operator.name());

        // I'm not really sure about this since we can compare by empty and null
        // if (operator.isBinary() && StringUtils.isBlank(value))
        // throw new IllegalArgumentException("Argument value can't be blank, null or empty for binary operators");

        this.columnName = columnName;
        this.operator = operator;
        this.values = values;
    }

    public String getColumnName () {
        return columnName;
    }

    public SelectParam setColumnName (String columnName) {
        this.columnName = columnName;
        return this;
    }

    public Operator getOperator () {
        return operator;
    }

    public SelectParam setOperator (Operator operator) {
        if (operator.isBoolean())
            throw new IllegalArgumentException("Invalid Operator " + operator.name());

        this.operator = operator;
        return this;
    }

    public String[] getValues () {
        return values;
    }

    public String getFirstValue () {
        return this.values[0];
    }

    public String getSecondValue () {
        return this.values[1];
    }

    public SelectParam setValues (String... values) {
        this.values = values;
        return this;
    }

    public String sql () {
        StringBuilder b = new StringBuilder(columnName).append(" ").append(operator.sql());

        if (operator.isBinary()) {
            b.append(" ?");
        }

        if (operator.equals(Operator.BETWEEN)) {
            b.append(" AND ?");
        }

        return b.toString();
    }
}
