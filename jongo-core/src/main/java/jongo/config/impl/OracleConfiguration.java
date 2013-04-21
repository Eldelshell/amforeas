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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jongo.  If not, see <http://www.gnu.org/licenses/>.
 */
package jongo.config.impl;

import jongo.jdbc.LimitParam;
import jongo.jdbc.OrderParam;
import jongo.sql.DynamicFinder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Oracle 8g and later DatabaseConfiguration implementation.
 * @author Alejandro Ayuso 
 */
@Deprecated
public class OracleConfiguration {
    
    private static final Logger l = LoggerFactory.getLogger(OracleConfiguration.class);
    
    public String getFirstRowQuery(String table) {
        if(StringUtils.isBlank(table))
            throw new IllegalArgumentException("Table name can't be blank, empty or null");
        return "SELECT * FROM " + table + " WHERE rownum = 0";
    }
    
    public String getSelectAllFromTableQuery(final String table, LimitParam limit, OrderParam order){
        if(StringUtils.isBlank(table) || limit == null || order == null)
            throw new IllegalArgumentException("Invalid argument");
        
        final StringBuilder query = new StringBuilder("SELECT * FROM ( SELECT ROW_NUMBER() OVER (ORDER BY ");
        query.append(order.getColumn());
        query.append(" ");
        query.append(order.getDirection());
        query.append(" )AS ROW_NUMBER, ");
        query.append(table);
        query.append(".* FROM ");
        query.append(table);
        query.append(" ) k WHERE ROW_NUMBER BETWEEN ");
        query.append(limit.getLimit());
        query.append(" AND ");
        query.append(limit.getStart());
        return query.toString();
    }
    
    public String getSelectAllFromTableQuery(final String table, final String idCol, LimitParam limit, OrderParam order){
        if(StringUtils.isBlank(table) || StringUtils.isBlank(idCol) || limit == null || order == null)
            throw new IllegalArgumentException("Invalid argument");
        
        final StringBuilder query = new StringBuilder("SELECT * FROM ( SELECT ROW_NUMBER() OVER (ORDER BY ");
        query.append(order.getColumn());
        query.append(" ");
        query.append(order.getDirection());
        query.append(" )AS ROW_NUMBER, ");
        query.append(table);
        query.append(".* FROM ");
        query.append(table);
        query.append(" WHERE ");
        query.append(idCol);
        
        query.append("= ? ) k WHERE ROW_NUMBER BETWEEN ");
        query.append(limit.getLimit());
        query.append(" AND ");
        query.append(limit.getStart());
        return query.toString();
    }

    public String getListOfTablesQuery() {
        return "SELECT TABLE_NAME FROM ALL_ALL_TABLES";
    }
    
    public String wrapDynamicFinderQuery(final DynamicFinder finder, LimitParam limit, OrderParam order){
        if(finder == null || limit == null || order == null)
            throw new IllegalArgumentException("Invalid argument");
        
        final String [] parts = finder.getSql().split("WHERE");
        final StringBuilder query = new StringBuilder("SELECT * FROM ( SELECT ROW_NUMBER() OVER (ORDER BY ");
        query.append(order.getColumn());
        query.append(" ");
        query.append(order.getDirection());
        query.append(" )AS ROW_NUMBER, ");
        query.append(finder.getTable());
        query.append(".* FROM ");
        query.append(finder.getTable());
        query.append(" WHERE ");
        query.append(parts[1]);
        query.append(" ) k WHERE ROW_NUMBER BETWEEN ");
        query.append(limit.getLimit());
        query.append(" AND ");
        query.append(limit.getStart());
        return query.toString();
    }
    
}
