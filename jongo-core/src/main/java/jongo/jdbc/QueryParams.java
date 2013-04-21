package jongo.jdbc;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang.StringUtils;

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

/**
 * Holder for a query parameters.
 * @author Alejandro Ayuso 
 */
@Deprecated
public class QueryParams {
    
    private String database;
    private String table;
    private String id;
    private String idField = "id";
    private Map<String, String> params = new HashMap<String, String>();
    private LimitParam limit;
    private OrderParam order;
    
    public QueryParams(){}

    public QueryParams(String database, String table, String id, String idField, Map<String, String> params, LimitParam limit, OrderParam order) {
        this.database = database;
        this.table = table;
        this.id = id;
        this.idField = idField;
        this.params = params;
        this.limit = limit;
        this.order = order;
    }
    
    public static QueryParams valueOf(String database, String table, String id, String idField, Map<String, String> params, LimitParam limit, OrderParam order){
        QueryParams p = new QueryParams();
        if(StringUtils.isBlank(database)){
            throw new IllegalArgumentException("Database argument can't be blank, null or empty");
        }else{
            p.setDatabase(database);
        }
        
        if(StringUtils.isBlank(table)){
            throw new IllegalArgumentException("Resource argument can't be blank, null or empty");
        }else{
            p.setTable(table);
        }
        
        if(StringUtils.isBlank(id)){
            p.setId("");
        }else{
            p.setId(id);
        }
        
        if(StringUtils.isBlank(idField)){
            p.setIdField("id");
        }else{
            p.setIdField(idField);
        }
        
        if(params != null){
            p.setParams(params);
        }
        
        if(limit == null){
            p.setLimit(new LimitParam());
        }else{
            p.setLimit(limit);
        }
        
        if(order == null){
            p.setOrder(new OrderParam(p.getIdField()));
        }else{
            p.setOrder(order);
        }
        
        return p;
    }
    
    public static QueryParams valueOf(String database, String table, String id, String idField, Map<String, String> params, LimitParam limit){
        return valueOf(database, table, id, idField, params, limit, null);
    }
    
    public static QueryParams valueOf(String database, String table, String id, String idField, Map<String, String> params, OrderParam order){
        return valueOf(database, table, id, idField, params, null, order);
    }
    
    public static QueryParams valueOf(String database, String table, String id, String idField, Map<String, String> params){
        return valueOf(database, table, id, idField, params, null, null);
    }
    
    public static QueryParams valueOf(String database, String table, String id, String idField, MultivaluedMap<String, String> params){
        QueryParams p = valueOf(database, table, id, idField, null, null, null);
        p.setParams(params);
        return p;
    }
    
    public static QueryParams valueOf(String database, String table, String id, String idField, LimitParam limit, OrderParam order){
        return valueOf(database, table, id, idField, null, limit, order);
    }
    
    public static QueryParams valueOf(String database, String table, String id, String idField, LimitParam limit){
        return valueOf(database, table, id, idField, null, limit, null);
    }
    
    public static QueryParams valueOf(String database, String table, String id, String idField, OrderParam order){
        return valueOf(database, table, id, idField, null, null, order);
    }
    
    public static QueryParams valueOf(String database, String table, String id, String idField){
        return valueOf(database, table, id, idField, null, null, null);
    }
    
    public static QueryParams valueOf(String database, String table, String id){
        return valueOf(database, table, id, null, null, null, null);
    }
    
    public static QueryParams valueOf(String database, String table){
        return valueOf(database, table, null, null, null, null, null);
    }
    
    public String getIdField() {
        return idField;
    }

    public String getDatabase() {
        return database;
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getTable() {
        return table;
    }

    public LimitParam getLimit() {
        return limit;
    }

    public OrderParam getOrder() {
        return order;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public void setLimit(LimitParam limit) {
        this.limit = limit;
    }

    public void setOrder(OrderParam order) {
        this.order = order;
    }

    public void setParams(Map<String, String> params) {
        this.params = new HashMap<String,String>();
        for(final Map.Entry<String, String> entry : params.entrySet()){
            final String k = entry.getKey(); 
            final String v = entry.getValue();
            if(v != null)
                this.params.put(k, v);
        }
    }
    
    public void setParams(final MultivaluedMap<String, String> formParams) {
        this.params = new HashMap<String,String>(); // clear the params first.
        for(String k : formParams.keySet()){
            String v = formParams.getFirst(k);
            if(v != null)
                this.params.put(k, v);
        }
    }
    
    public void setParam(final String k, final String v){
        this.params.put(k,v);
    }

    public void setTable(String table) {
        this.table = table;
    }
    
    public boolean isValid(){
        return this.database != null && this.table != null && this.params != null && !this.params.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QueryParams other = (QueryParams) obj;
        if ((this.database == null) ? (other.database != null) : !this.database.equals(other.database)) {
            return false;
        }
        if ((this.table == null) ? (other.table != null) : !this.table.equals(other.table)) {
            return false;
        }
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.idField == null) ? (other.idField != null) : !this.idField.equals(other.idField)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.database != null ? this.database.hashCode() : 0);
        hash = 17 * hash + (this.table != null ? this.table.hashCode() : 0);
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 17 * hash + (this.idField != null ? this.idField.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("[");
        b.append(id); 
        if(!this.params.values().isEmpty()){
            b.append(", ");
            b.append(StringUtils.join(this.params.values(), ","));
        }
        b.append("]");
        return b.toString();
    }
    
    
}
