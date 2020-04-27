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

package amforeas;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import amforeas.config.AmforeasConfiguration;

/**
 * Main class of Amforeas. Reads configuration file and starts jetty embedded.
 * @author Alejandro Ayuso
 */
public class AmforeasJetty {

    private static final Logger l = LoggerFactory.getLogger(AmforeasJetty.class);

    public static void main (String[] args) throws Exception {
        l.debug("Loading Configuration");

        AmforeasConfiguration conf = AmforeasConfiguration.instanceOf();

        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(conf.getServerThreadsMin());
        threadPool.setMaxThreads(conf.getServerThreadsMax());

        Server server = new Server(threadPool);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, conf.getServerRoot());
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "amforeas.rest");

        server.setHandler(context);

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(conf.getServerPort());
        connector.setHost(conf.getServerHost());
        server.addConnector(connector);

        l.info("Starting Amforeas in Jetty Embedded mode");
        server.start();
        server.setStopAtShutdown(true);
        server.join();
    }

}
