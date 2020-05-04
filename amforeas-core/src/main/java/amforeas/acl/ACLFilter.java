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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public enum ACLFilter {

    /**
     * Allow all operations
     */
    ALL,

    /**
     * Deny all operations
     */
    NONE,

    /**
     * Allow meta operations
     */
    META,

    /**
     * Allow read operations on resources
     */
    READ,

    /**
     * Allow insert operations on resources
     */
    INSERT,

    /**
     * Allow update operations on resources
     */
    UPDATE,

    /**
     * Allow delete operations on resources
     */
    DELETE,

    /**
     * Allow execution of stored procedures or functions an alias.
     */
    EXEC;

    public static Set<ACLFilter> parse (String property) {
        if (StringUtils.isEmpty(property)) {
            return new HashSet<ACLFilter>();
        }

        Set<ACLFilter> keywords = Arrays.asList(property.split(","))
            .stream()
            .map(p -> ACLFilter.valueOf(p.trim().toUpperCase()))
            .collect(Collectors.toSet());

        if (keywords.contains(ALL) && keywords.size() > 1) {
            throw new IllegalArgumentException("Invalid ACL rule property " + property + ". 'All' rule has to be alone.");
        }

        if (keywords.contains(NONE) && keywords.size() > 1) {
            throw new IllegalArgumentException("Invalid ACL rule property " + property + ". 'None' rule has to be alone.");
        }

        return keywords;
    }

}
