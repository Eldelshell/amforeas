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
package amforeas.jdbc;

import java.lang.reflect.Field;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import amforeas.exceptions.AmforeasBadRequestException;

/**
 * An object which holds data about a function/procedure parameters.
 */
@XmlRootElement
public class StoredProcedureParam {

    @XmlTransient
    private static final Field[] fields = java.sql.Types.class.getFields();

    @XmlTransient
    private static final Logger l = LoggerFactory.getLogger(StoredProcedureParam.class);

    private String value;
    private String name;
    private boolean outParameter;
    private String type;
    private Integer index;

    public StoredProcedureParam() {}

    public StoredProcedureParam(String name, String value, boolean outParameter, Integer index, String type) {
        this.value = value;
        this.name = name;
        this.outParameter = outParameter;
        this.index = index;
        this.type = type;
    }

    public String getName () {
        return name;
    }

    public boolean isOutParameter () {
        return outParameter;
    }

    public String getType () {
        return type;
    }

    public String getValue () {
        return value;
    }

    public Integer getIndex () {
        return index;
    }

    public void setIndex (Integer index) {
        this.index = index;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setOutParameter (boolean outParameter) {
        this.outParameter = outParameter;
    }

    public void setType (String type) {
        this.type = type;
    }

    public void setValue (String value) {
        this.value = value;
    }

    @XmlTransient
    @JsonIgnore
    public synchronized Integer getSqlType () throws AmforeasBadRequestException {
        l.debug("Parsing SQL Type from {}", this.type);

        for (final Field f : fields) {
            if (!f.getName().equals(this.type.toUpperCase())) {
                continue;
            }

            try {
                return f.getInt(null);
            } catch (IllegalArgumentException e) {
                l.warn("IllegalArgumentException when obtaining java.sql.Types with reflection");
            } catch (IllegalAccessException e) {
                l.warn("IllegalAccessException when obtaining java.sql.Types with reflection");
            }
        }

        throw new AmforeasBadRequestException("Invalid SQL type for stored procedure parameter: " + this.type);
    }

    @Override
    public String toString () {
        StringBuilder b = new StringBuilder("StoredProcedureParam [");
        b.append(" value=");
        b.append(value);
        b.append(" name=");
        b.append(name);
        b.append(" outParameter=");
        b.append(outParameter);
        b.append(" type=");
        b.append(type);
        b.append(" index=");
        b.append(index);
        b.append("]");
        return b.toString();
    }

    @Override
    public int hashCode () {
        return new HashCodeBuilder()
            .append(name)
            .append(index)
            .append(outParameter)
            .append(type.toUpperCase())
            .append(value)
            .toHashCode();
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        if (obj == this)
            return true;

        return obj.hashCode() == this.hashCode();
    }

}
