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

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
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

        final AmforeasConfiguration conf = AmforeasConfiguration.instanceOf();

        final QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(conf.getServerThreadsMin());
        threadPool.setMaxThreads(conf.getServerThreadsMax());

        final Server server = new Server(threadPool);

        setupJerseyServlet(conf, server);
        setupHTTPConnection(conf, server);
        setupHTTPSConnection(conf, server);

        l.info("Starting Amforeas in Jetty Embedded mode");
        server.start();
        server.setStopAtShutdown(true);
        server.join();
    }

    private static void setupJerseyServlet (final AmforeasConfiguration conf, final Server server) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, conf.getServerRoot());
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "amforeas.rest");

        server.setHandler(context);
    }

    private static void setupHTTPConnection (final AmforeasConfiguration conf, final Server server) {
        final Integer port = conf.getServerPort();

        if (port == null || port == 0) {
            l.debug("Invalid HTTP configuration {}", port);
            return;
        }

        l.info("Listening on HTTP port {}", port);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        connector.setHost(conf.getServerHost());
        server.addConnector(connector);
    }

    private static void setupHTTPSConnection (final AmforeasConfiguration conf, final Server server) {
        final Integer port = conf.getSecurePort();
        final String path = conf.getJKSFile();
        final String pwd = conf.getJKSFilePassword();

        if (port == null || port == 0) {
            l.debug("Invalid HTTPS configuration {}", port);
            return;
        }

        if (StringUtils.isNotEmpty(path) && StringUtils.isEmpty(pwd)) {
            l.debug("No valid password provided for {}. Set the correct value for amforeas.server.https.jks.password", path);
            return;
        }

        if (StringUtils.isEmpty(path) && StringUtils.isNotEmpty(pwd)) {
            l.debug("No valid JKS file provided. Set the correct value for amforeas.server.https.jks", path);
            return;
        }

        l.debug("Reading JKS from {}", path);
        File jks = new File(path);

        if (!jks.exists() || !jks.canRead()) {
            l.warn("Can't read JKS file on {}", path);
            return;
        }

        l.info("Listening on HTTPS port {}", port);
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(jks.getAbsolutePath());
        sslContextFactory.setKeyStorePassword(pwd);

        ServerConnector https = new ServerConnector(server, sslContextFactory);
        https.setPort(port);
        https.setHost(conf.getServerHost());
        server.addConnector(https);
    }

}
