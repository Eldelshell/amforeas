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
package amforeas.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import amforeas.AmforeasShutdown;
import amforeas.config.AmforeasConfiguration;

public class DemoConfiguration extends AmforeasConfiguration {

    private static final Logger l = LoggerFactory.getLogger(DemoConfiguration.class);

    public DemoConfiguration() {
        super();
    }

    public synchronized void load () {
        l.debug("Registering the shutdown hook!");
        Runtime.getRuntime().addShutdownHook(new AmforeasShutdown());

        l.debug("Loading demo configuration with memory databases");
        this.databases = Demo.getDemoDatabasesConfiguration();
        Demo.generateDemoDatabases(this.getDatabases());

        if (!this.isValid())
            throw new IllegalStateException("Configuration is not valid");
    }

}
