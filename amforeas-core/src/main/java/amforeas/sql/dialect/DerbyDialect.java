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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import amforeas.sql.Select;

/**
 * Dialect for Apache Derby.
 */
public class DerbyDialect extends SQLDialect {

    private static final Logger l = LoggerFactory.getLogger(DerbyDialect.class);

    @Override
    public String toStatementString (final Select select) {
        final StringBuilder b = new StringBuilder("SELECT ");
        if (select.getLimitParam() == null) {
            return super.toStatementString(select);
        } else {
            b.append("* FROM ( SELECT ROW_NUMBER() OVER () AS ROW_NUM, ");

            if (select.isAllColumns()) {
                b.append("t.*");
            } else {
                appendColumns(b, select);
            }

            b.append(" FROM ").append(select.getTable().toString()).append(" t");

            if (!select.isAllRecords()) {
                appendWhereClause(b, select);
            }

            b.append(") AS tmp WHERE ROW_NUM BETWEEN ").append(select.getLimitParam().getStart());
            b.append(" AND ").append(select.getLimitParam().getLimit());

            b.append(" ORDER BY tmp.");
            if (select.getOrderParam() == null) {
                b.append(select.getTable().getPrimaryKey());
            } else {
                b.append(select.getOrderParam().getColumn()).append(" ").append(select.getOrderParam().getDirection());
            }
        }
        l.debug(b.toString());
        return b.toString();
    }

    @Override
    public String listOfTablesStatement () {
        return "SELECT * FROM SYS.SYSTABLES WHERE tabletype = 'T'";
    }

}
