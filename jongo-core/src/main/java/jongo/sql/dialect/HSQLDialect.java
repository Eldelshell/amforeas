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
package amforeas.sql.dialect;

import amforeas.sql.Select;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialect for HSQLDB, Hypersonic or whatever is called today.
 * @author Alejandro Ayuso <alejandroayuso@gmail.com>
 */
public class HSQLDialect extends SQLDialect{
    
    private static final Logger l = LoggerFactory.getLogger(HSQLDialect.class);

    @Override
    public String toStatementString(Select select) {
        final StringBuilder b = new StringBuilder("SELECT ");
        if(select.isAllColumns()){
            b.append("*");
        }else{
            String cols = StringUtils.join(select.getColumns(), ",");
            b.append(cols);
        }
        b.append(" FROM ").append(select.getTable().getName());
        if(!select.isAllRecords()){
            b.append(" WHERE ").append(select.getParameter().getColumnName()).append(" ").append(select.getParameter().getOperator().sql()).append(" ?");
        }
        if(select.getOrderParam() != null)
            b.append(" ORDER BY ").append(select.getOrderParam().toString());
        
        if(select.getLimitParam() != null)
            b.append(" LIMIT ").append(select.getLimitParam().getLimit()).append(" OFFSET ").append(select.getLimitParam().getStart());
        
        l.debug(b.toString());
        return b.toString();
    }

    @Override
    public String listOfTablesStatement() {
        return "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE table_type = 'TABLE'";
    }
    
}
