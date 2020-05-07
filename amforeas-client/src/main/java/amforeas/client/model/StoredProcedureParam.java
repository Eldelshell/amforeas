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
package amforeas.client.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * An object which holds data about a function/procedure parameters.
 */
@XmlRootElement
public class StoredProcedureParam {

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
