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

import amforeas.SingletonFactoryImpl;
import amforeas.SingletonFactory;
import amforeas.config.AmforeasConfiguration;

public class ACLManager {

    private final SingletonFactory factory;

    public ACLManager() {
        this.factory = new SingletonFactoryImpl();
    }

    public ACLManager(SingletonFactory factory) {
        this.factory = factory;
    }

    public Boolean validate (final String alias, ACLFilter keyword) {
        final AmforeasConfiguration conf = this.factory.getConfiguration();
        final ACLRule rule = conf.getAliasRule(alias);

        if (rule.getRules().size() == 1 && rule.getRules().contains(ACLFilter.ALL)) {
            return true;
        }

        return rule.getRules().contains(keyword);
    }

    public Boolean validate (final String alias, final String resource, ACLFilter keyword) {
        final AmforeasConfiguration conf = this.factory.getConfiguration();
        final ACLRule rule = conf.getResourceRules(alias, resource);

        if (rule.getRules().size() == 1 && rule.getRules().contains(ACLFilter.ALL)) {
            return true;
        }

        return rule.getRules().contains(keyword);
    }

}
