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
package amforeas.jdbc;

import java.lang.reflect.Field;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import amforeas.exceptions.AmforeasBadRequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An object which holds data about a function/procedure parameters.
 * @author Alejandro Ayuso 
 */
@XmlRootElement
public class StoredProcedureParam {
    
    @XmlTransient private static final Field [] fields = java.sql.Types.class.getFields();
    @XmlTransient private static final Logger l = LoggerFactory.getLogger(StoredProcedureParam.class);
    
    private String value;
    private String name;
    private boolean outParameter;
    private Integer type;
    private Integer index;
    
    public StoredProcedureParam(){}
    
    public StoredProcedureParam(String name, String value, boolean outParameter, Integer index, Integer type) {
        this.value = value;
        this.name = name;
        this.outParameter = outParameter;
        this.index = index;
        this.type = type;
    }
    
    public StoredProcedureParam(String name, String value, boolean outParameter, Integer index, String type) throws AmforeasBadRequestException {
        this.value = value;
        this.name = name;
        this.outParameter = outParameter;
        this.index = index;
        this.type = getSqlType(type);
    }
    
    public String getName() {
        return name;
    }

    public boolean isOutParameter() {
        return outParameter;
    }

    public Integer getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOutParameter(boolean outParameter) {
        this.outParameter = outParameter;
    }

    public void setType(String type) throws AmforeasBadRequestException {
        this.type = getSqlType(type);
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public static synchronized Integer getSqlType(final String type) throws AmforeasBadRequestException{
        l.debug("Parsing SQL Type from " + type);
        Integer ret = null;
        for(Field f : fields){
            if(f.getName().equalsIgnoreCase(type)){
                try {
                    ret = f.getInt(java.sql.Types.class);
                } catch (IllegalArgumentException ex) {
                    l.error(ex.getMessage());
                    throw new AmforeasBadRequestException("Invalid SQL Type: " + type + ". More info at http://docs.oracle.com/javase/6/docs/api/java/sql/Types.html");
                } catch (IllegalAccessException ex) {
                    l.error(ex.getMessage()); //this should't happen :)
                }
            }
        }
        return ret;    
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("StoredProcedureParam [");
        b.append(" value="); b.append(value);
        b.append(" name="); b.append(name);
        b.append(" outParameter="); b.append(outParameter);
        b.append(" type="); b.append(type);
        b.append(" index="); b.append(index);
        b.append("]");
        return b.toString();
    }
}
