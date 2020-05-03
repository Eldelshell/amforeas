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

package amforeas.acl;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ACLRule {

    private final String alias;
    private final String resource;
    private Set<ACLFilter> rules;

    public static ACLRule of (String alias, String rules) {
        return new ACLRule(alias, null, ACLFilter.parse(rules));
    }

    public static ACLRule of (String alias, String resource, String rules) {
        return new ACLRule(alias, resource, ACLFilter.parse(rules));
    }

    public ACLRule(String alias, String resource, Set<ACLFilter> rules) {
        this.alias = alias;
        this.resource = resource;
        this.rules = rules;
    }

    public Set<ACLFilter> getRules () {
        return rules;
    }

    public void setRules (Set<ACLFilter> rules) {
        this.rules = rules;
    }

    public String getAlias () {
        return alias;
    }

    public Optional<String> getResource () {
        return Optional.ofNullable(resource);
    }

    @Override
    public int hashCode () {
        return Objects.hash(alias, resource, rules);
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
        ACLRule other = (ACLRule) obj;
        return other.hashCode() == this.hashCode();
    }

    @Override
    public String toString () {
        return "ACLRule [alias=" + alias + ", resource=" + resource + ", rules=" + rules + "]";
    }

}
