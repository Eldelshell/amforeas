package amforeas.demo;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import amforeas.AmforeasJetty;
import amforeas.SingletonFactory;
import amforeas.config.AmforeasConfiguration;

public class DemoJetty {

    private static final Logger l = LoggerFactory.getLogger(AmforeasJetty.class);

    public static void main (String[] args) throws Exception {
        System.out.println("*************************************************");
        System.out.println("*                                               *");
        System.out.println("*            Starting Amforeas Demo             *");
        System.out.println("*                                               *");
        System.out.println("*************************************************");

        SingletonFactory factory = new DemoSingletonFactory();
        final AmforeasConfiguration conf = factory.getConfiguration();

        final Server server = new Server();

        setupJerseyServlet(conf, server);
        setupHTTPConnection(conf, server);

        server.start();
        server.setStopAtShutdown(true);
        server.join();
    }

    private static void setupJerseyServlet (final AmforeasConfiguration conf, final Server server) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, conf.getServerRoot());
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "amforeas.rest, amforeas.filter");

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

}
