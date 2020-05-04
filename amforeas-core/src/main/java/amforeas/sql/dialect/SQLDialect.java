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

package amforeas.sql.dialect;

import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;
import amforeas.sql.Delete;
import amforeas.sql.DynamicFinder;
import amforeas.sql.Insert;
import amforeas.sql.Select;
import amforeas.sql.Update;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Dialect representation of standard SQL98 & SQL2008 operations.
 */
public class SQLDialect implements Dialect {

    private static final Logger l = LoggerFactory.getLogger(SQLDialect.class);

    protected final String INSERT_FORMAT = "INSERT INTO %s (%s) VALUES (%s)";
    protected final String UPDATE_FORMAT = "UPDATE %s SET %s WHERE %s";
    protected final String DELETE_FORMAT = "DELETE FROM %s WHERE %s";

    @Override
    public String toStatementString (final Insert insert) {
        if (insert.getColumns().isEmpty())
            throw new IllegalArgumentException("An insert query can't be empty");

        String cols = StringUtils.join(insert.getColumns().keySet(), ",");
        String args = StringUtils.removeEnd(StringUtils.repeat("?,", insert.getColumns().size()), ",");
        String sql = String.format(INSERT_FORMAT, insert.getTable().getName(), cols, args);

        l.debug(sql);
        return sql;
    }

    @Override
    public String toStatementString (final Select select) {
        final StringBuilder b = new StringBuilder("SELECT ");

        if (select.getLimitParam() == null) {
            if (select.isAllColumns()) {
                b.append("t.*");
            } else {
                appendColumns(b, select);
            }
            b.append(" FROM ").append(select.getTable().toString()).append(" t");
            if (!select.isAllRecords()) {
                appendWhereClause(b, select);
            }
            if (select.getOrderParam() != null) {
                b.append(" ORDER BY t.");
                b.append(select.getOrderParam().getColumn()).append(" ").append(select.getOrderParam().getDirection());
            }
        } else {
            b.append("* FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY ");

            if (select.getOrderParam() == null) {
                b.append("t.");
                b.append(select.getTable().getPrimaryKey());
            } else {
                b.append("t.");
                b.append(select.getOrderParam().getColumn()).append(" ").append(select.getOrderParam().getDirection());
            }

            b.append(" ) AS ROW_NUMBER, ");
            if (select.isAllColumns()) {
                b.append("t.*");
            } else {
                appendColumns(b, select);
            }
            b.append(" FROM ").append(select.getTable().toString()).append(" t");
            if (!select.isAllRecords()) {
                appendWhereClause(b, select);
            }
            b.append(") WHERE ROW_NUMBER BETWEEN ").append(select.getLimitParam().getStart()).append(" AND ").append(select.getLimitParam().getLimit());
        }

        l.debug(b.toString());
        return b.toString();
    }

    @Override
    public String toStatementString (final Update update) {
        if (update.getColumns().isEmpty())
            throw new IllegalArgumentException("An update query can't be empty");

        String vals = StringUtils.join(update.getColumns().keySet(), "=?,") + "=?";
        String args = update.getTable().getPrimaryKey() + "=?";
        String sql = String.format(UPDATE_FORMAT, update.getTable().getName(), vals, args);

        l.debug(sql);
        return sql;
    }

    @Override
    public String toStatementString (final Delete delete) {
        String args = delete.getTable().getPrimaryKey() + "=?";
        String sql = String.format(DELETE_FORMAT, delete.getTable().getName(), args);

        l.debug(sql);
        return sql;
    }

    @Override
    public String toStatementString (final DynamicFinder finder, final LimitParam limit, final OrderParam order) {
        if (finder == null || limit == null || order == null)
            throw new IllegalArgumentException("Invalid argument");
        final StringBuilder b = new StringBuilder(finder.getSql());
        b.append(" ORDER BY ");
        b.append(order.getColumn());
        b.append(" ");
        b.append(order.getDirection());
        b.append(" LIMIT ");
        b.append(limit.getLimit());
        b.append(" OFFSET ");
        b.append(limit.getStart());
        l.debug(b.toString());
        return b.toString();
    }

    @Override
    public String listOfTablesStatement () {
        throw new UnsupportedOperationException("Operation not supported");
    }

    protected StringBuilder appendWhereClause (final StringBuilder b, Select select) {
        b.append(" WHERE t.").append(select.getParameter().sql());
        return b;
    }

    protected void appendColumns (final StringBuilder b, Select select) {
        for (String col : select.getColumns())
            b.append("t.").append(col).append(",");
        b.deleteCharAt(b.length() - 1);
    }

}
