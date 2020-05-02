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

package amforeas.config;

import java.util.Objects;

public class AmforeasProperty {

    public static final String PREFIX = "amforeas.";

    private final String key;
    private final Boolean required;
    private String value;

    public AmforeasProperty(String key, Boolean required) {
        this.key = PREFIX + key;
        this.required = required;
    }

    public AmforeasProperty(String key, Boolean required, String value) {
        this.key = PREFIX + key;
        this.required = required;
        this.value = value;
    }

    public String getValue () {
        return value;
    }

    public void setValue (String value) {
        this.value = value;
    }

    public String getKey () {
        return key;
    }

    public Boolean isRequired () {
        return required;
    }

    @Override
    public int hashCode () {
        return Objects.hash(key, required, value);
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AmforeasProperty other = (AmforeasProperty) obj;
        return Objects.equals(key, other.key) && Objects.equals(required, other.required) && Objects.equals(value, other.value);
    }

}
