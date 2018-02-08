package ua.danit.rest.core;

/**
 * Entry point for every REST application.
 *
 * @author Andrey Minov
 */
public class RestApplication {

  private final int port;
  private final String matchingUrl;
  private final ServicesStore servicesStore;

  /**
   * Instantiates a new REST application.
   *
   * @param port          the port where application is running.
   * @param matchingUrl   the matching url to register servlet.
   * @param servicesStore the resources store for storing resources instances.
   */
  RestApplication(int port, String matchingUrl, ServicesStore servicesStore) {
    this.port = port;
    this.matchingUrl = matchingUrl;
    this.servicesStore = servicesStore;
  }

  /**
   * Start REST application.
   *
   * @param join true when current thread have to wait until server is stopped.
   * @throws Exception the exception occurred on start of application.
   */
  public void start(boolean join) throws Exception {
    throw new UnsupportedOperationException("Start method is not implemented!");
  }

  /**
   * Start REST application and join on current thread until server is stopped.
   *
   * @throws Exception the exception occurred on start of application.
   */
  public void start() throws Exception {
    start(true);
  }

  /**
   * Stop REST application.
   *
   * @throws Exception the exception occurred on stop of application.
   */
  public void stop() throws Exception {
    throw new UnsupportedOperationException("Stop method is not implemented!");
  }


  protected int getPort() {
    return port;
  }

  protected String getMatchingUrl() {
    return matchingUrl;
  }

  protected ServicesStore getServicesStore() {
    return servicesStore;
  }
}
