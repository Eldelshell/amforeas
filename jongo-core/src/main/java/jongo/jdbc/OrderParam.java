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

package jongo.jdbc;

import javax.ws.rs.core.MultivaluedMap;

import jongo.sql.Table;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Alejandro Ayuso 
 */
public class OrderParam {
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    
    private String column;
    private String direction;
    
    public OrderParam(){
        this.column = "id";
        this.direction = ASC;
    }
    
    public OrderParam(final Table table){
        this.column = table.getPrimaryKey();
        this.direction = ASC;
    }
    
    public OrderParam(String col){
        if(StringUtils.isBlank(col))
            throw new IllegalArgumentException("Invalid column parameter");
        
        if(ASC.equalsIgnoreCase(col) || DESC.equalsIgnoreCase(col))
            throw new IllegalArgumentException("Invalid column parameter");
        
        this.column = StringUtils.deleteWhitespace(col);
        this.direction = ASC;
    }
    
    public OrderParam(String col, String dir){
        if(StringUtils.isBlank(dir) || StringUtils.isBlank(col))
            throw new IllegalArgumentException("Invalid order parameters");
        
        if(ASC.equalsIgnoreCase(dir)){
            this.direction = ASC;
        }else if(DESC.equalsIgnoreCase(dir)){
            this.direction = DESC;
        }else{
            throw new IllegalArgumentException("Invalid direction parameter");
        }
        this.column = StringUtils.deleteWhitespace(col);
    }
    
    public String getColumn() {
        return column;
    }

    public String getDirection() {
        return direction;
    }
    
    public static OrderParam valueOf(final MultivaluedMap<String, String> pathParams){
        return valueOf(pathParams, "id");
    }
    
    public static OrderParam valueOf(final MultivaluedMap<String, String> pathParams, final String pk){
        String sort = pathParams.getFirst("sort");
        String dir = pathParams.getFirst("dir");
        
        if(StringUtils.isEmpty(dir)) dir = "ASC";
        
        OrderParam instance;
        if(StringUtils.isEmpty(sort)){
            instance = new OrderParam(pk, dir);
        }else{
            instance = new OrderParam(sort, dir);
        }
        
        return instance;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    @Override
    public String toString(){
        StringBuilder b = new StringBuilder(getColumn());
        b.append(" ");
        b.append(direction);
        return b.toString();
    }
}
