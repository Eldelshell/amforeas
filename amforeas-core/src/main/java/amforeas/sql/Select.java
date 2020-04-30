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

import java.util.ArrayList;
import java.util.List;

import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;

/**
 * Represents a SQL SELECT statement.
 */
public class Select {

    /**
     * Where the select operation is to be performed.
     */
    private final Table table;

    /**
     * {@link amforeas.sql.SelectParam} of the query.
     */
    private SelectParam parameter;

    /**
     * Generates the ORDER BY part.
     */
    private OrderParam orderParam;

    /**
     * Limit the number of results.
     */
    private LimitParam limitParam;

    /**
     * List of columns to be returned. If no columns are provided, we use the * operator.
     */
    private List<String> columns = new ArrayList<String>();


    public Select(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Table can't be null");
        }
        this.table = table;
    }

    public Select setOrderParam (OrderParam param) {
        this.orderParam = param;
        return this;
    }

    public Select setLimitParam (LimitParam param) {
        this.limitParam = param;
        return this;
    }

    public Select setColumns (List<String> columns) {
        for (String col : columns)
            addColumn(col);
        return this;
    }

    public Select addColumn (String column) {
        this.columns.add(column);
        return this;
    }

    public List<String> getColumns () {
        return columns;
    }

    public LimitParam getLimitParam () {
        return limitParam;
    }

    public OrderParam getOrderParam () {
        return orderParam;
    }

    public Table getTable () {
        return table;
    }

    /**
     * Should the SELECT statement return all records or only a subset.
     * @return true if we're looking for all records or false if we are
     * only after one record provided by the <i>value</i> property.
     */
    public boolean isAllRecords () {
        return this.parameter == null;
    }

    /**
     * Should the SELECT statement return all columns (*) or a subset.
     * @return true for a SELECT * statement. False for a SELECT a,b,c statement.
     */
    public boolean isAllColumns () {
        return columns.isEmpty();
    }

    public SelectParam getParameter () {
        return parameter;
    }

    public Select setParameter (SelectParam parameter) {
        this.parameter = parameter;
        return this;
    }

    @Override
    public String toString () {
        return "Select{" + "table=" + table + ", parameter=" + parameter + ", orderParam=" + orderParam + ", limitParam=" + limitParam + ", columns=" + columns + '}';
    }
}
