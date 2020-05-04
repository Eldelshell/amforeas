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
 * Dialect for H2 RDBMS.
 * @author samarjit samanta
 */
public class H2Dialect extends SQLDialect {

    private static final Logger l = LoggerFactory.getLogger(OracleDialect.class);


    @Override
    public String toStatementString (Insert insert) {
        if (insert.getColumns().isEmpty())
            throw new IllegalArgumentException("An insert query can't be empty");

        final StringBuilder b = new StringBuilder("INSERT INTO ");
        b.append(insert.getTable().getDatabase()).append(".");
        b.append(insert.getTable().getName());
        if (!insert.getColumns().isEmpty()) {
            b.append(" (");
            b.append(StringUtils.join(insert.getColumns().keySet(), ","));
            b.append(") VALUES (");
            b.append(StringUtils.removeEnd(StringUtils.repeat("?,", insert.getColumns().size()), ","));
            b.append(")");
        }
        l.debug(b.toString());
        return b.toString();
    }

    @Override
    public String toStatementString (Update update) {
        if (update.getColumns().isEmpty())
            throw new IllegalArgumentException("An update query can't be empty");

        final StringBuilder b = new StringBuilder("UPDATE ");
        b.append(update.getTable().getDatabase()).append(".");
        b.append(update.getTable().getName()).append(" SET ");

        for (String k : update.getColumns().keySet()) {
            b.append(update.getTable().getName()).append(".");
            b.append(k);
            b.append("=?,");
        }

        b.deleteCharAt(b.length() - 1);
        b.append(" WHERE ");
        b.append(update.getTable().getName()).append(".");
        b.append(update.getTable().getPrimaryKey()).append("=?");
        l.debug(b.toString());
        return b.toString();
    }

    @Override
    public String toStatementString (Delete delete) {
        final StringBuilder b = new StringBuilder("DELETE FROM ");
        b.append(delete.getTable().getDatabase()).append(".");
        b.append(delete.getTable().getName());
        b.append(" WHERE ");
        b.append(delete.getTable().getName()).append(".");
        b.append(delete.getTable().getPrimaryKey()).append("=?");
        l.debug(b.toString());
        return b.toString();
    }

    @Override
    public String toStatementString (Select select) {
        final StringBuilder b = new StringBuilder("SELECT ");
        if (select.isAllColumns()) {
            b.append("*");
        } else {
            appendColumns(b, select, null);
        }
        b.append(" FROM ").append(select.getTable().getName());
        if (!select.isAllRecords()) {
            b.append(" WHERE ").append(select.getParameter().sql());
        }
        if (select.getOrderParam() != null)
            b.append(" ORDER BY ").append(select.getOrderParam().toString());

        if (select.getLimitParam() != null)
            b.append(" LIMIT ").append(select.getLimitParam().getLimit()).append(" OFFSET ").append(select.getLimitParam().getStart());

        l.debug(b.toString());
        return b.toString();
    }

    @Override
    public String toStatementString (DynamicFinder finder, LimitParam limit, OrderParam order) {
        if (finder == null || limit == null || order == null)
            throw new IllegalArgumentException("Invalid argument");

        final String[] parts = finder.getSql().split("WHERE");
        final StringBuilder query = new StringBuilder("SELECT * FROM ( SELECT ROWnum ");
        // query.append(finder.getTable()).append(".").append(order.getColumn());
        // query.append(" ");
        query.append(order.getDirection());
        query.append("   AS ROW_NUMBER, ");
        query.append(finder.getTable());
        query.append(".* FROM ");
        query.append(finder.getTable());
        query.append(" WHERE ");
        query.append(parts[1]);
        query.append(" ) WHERE ROW_NUMBER BETWEEN ");
        query.append(limit.getStart());
        query.append(" AND ");
        query.append(limit.getLimit());
        return query.toString();
    }

    @Override
    public String listOfTablesStatement () {
        return "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  WHERE table_type = 'TABLE'";
    }

}

