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

package org.amforeas.config;

import java.util.Properties;
import amforeas.config.AmforeasConfiguration;

public class AmforeasConfigurationStub extends AmforeasConfiguration {

    private final Properties javaProperties = new Properties();

    public void loadProperties () {
        javaProperties.clear();

        javaProperties.setProperty("amforeas.server.root", "/amforeas/*");
        javaProperties.setProperty("amforeas.server.host", "0.0.0.0");
        javaProperties.setProperty("amforeas.server.http.port", "8080");

        javaProperties.setProperty("amforeas.alias.list", "alias1, alias2, alias3, alias4");

        javaProperties.setProperty("amforeas.alias1.jdbc.driver", "H2_MEM");
        javaProperties.setProperty("amforeas.alias1.jdbc.database", "test_db");

        javaProperties.setProperty("amforeas.alias2.jdbc.driver", "MSSQL_JTDS");
        javaProperties.setProperty("amforeas.alias2.jdbc.database", "test_db2");
        javaProperties.setProperty("amforeas.alias2.acl.allow", "none");
        javaProperties.setProperty("amforeas.alias2.acl.rules.cars.allow", "insert, delete");

        javaProperties.setProperty("amforeas.alias3.jdbc.driver", "H2_MEM");
        javaProperties.setProperty("amforeas.alias3.jdbc.database", "test_db");
        javaProperties.setProperty("amforeas.alias3.acl.allow", "meta, read, update");
        javaProperties.setProperty("amforeas.alias3.acl.rules.users.allow", "none");
        javaProperties.setProperty("amforeas.alias3.acl.rules.movies.allow", "insert, delete");
        javaProperties.setProperty("amforeas.alias3.acl.rules.cats.allow", "all");

        javaProperties.setProperty("amforeas.alias4.jdbc.driver", "MSSQL_JTDS");
        javaProperties.setProperty("amforeas.alias4.jdbc.database", "test_db2");
        javaProperties.setProperty("amforeas.alias4.acl.allow", "meta, exec");

        this.properties.load(javaProperties);
    }

}
