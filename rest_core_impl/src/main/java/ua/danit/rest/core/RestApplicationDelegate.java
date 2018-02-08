package ua.danit.rest.core;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Entry point for every REST application.
 *
 * @author Andrey Minov
 */
public class RestApplicationDelegate extends RestApplication {
  private Server server;
  private DispatcherServlet dispatcherServlet;

  /**
   * Instantiates a new Rest application from built application..
   *
   * @param application the application prepared to start.
   */
  public RestApplicationDelegate(RestApplication application) {
    super(application.getPort(), application.getMatchingUrl(), application.getServicesStore());
    this.dispatcherServlet = new DispatcherServlet(getServicesStore());
  }

  @Override
  public void start(boolean join) throws Exception {
    server = new Server(getPort());
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);
    context.addServlet(new ServletHolder(dispatcherServlet), getMatchingUrl());
    server.start();
    if (join) {
      server.join();
    }
  }

  @Override
  public void stop() throws Exception {
    server.stop();
  }
}
