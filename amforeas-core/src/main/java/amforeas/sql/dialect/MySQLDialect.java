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

import amforeas.sql.Select;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialect for MySQL, Nettezza or MariaDB.
 */
public class MySQLDialect extends SQLDialect {

    private static final Logger l = LoggerFactory.getLogger(MySQLDialect.class);

    @Override
    public String listOfTablesStatement () {
        return "SHOW TABLES";
    }

    @Override
    public String toStatementString (Select select) {
        final StringBuilder b = new StringBuilder("SELECT ");
        if (select.isAllColumns()) {
            b.append("t.*");
        } else {
            appendColumns(b, select);
        }
        b.append(" FROM ").append(select.getTable().getDatabase()).append(".");
        b.append(select.getTable().getName()).append(" t");
        if (!select.isAllRecords()) {
            super.appendWhereClause(b, select);
        }
        if (select.getOrderParam() != null)
            b.append(" ORDER BY t.").append(select.getOrderParam().getColumn()).append(" ").append(select.getOrderParam().getDirection());

        if (select.getLimitParam() != null)
            b.append(" LIMIT ").append(select.getLimitParam().getStart()).append(",").append(select.getLimitParam().getLimit());

        l.debug(b.toString());
        return b.toString();
    }

}
