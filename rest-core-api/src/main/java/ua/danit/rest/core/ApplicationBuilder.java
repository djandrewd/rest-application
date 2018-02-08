package ua.danit.rest.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.RuntimeDelegate;

import ua.danit.rest.core.convertors.ConvertersStore;
import ua.danit.rest.core.ext.RestRuntimeDelegate;
import ua.danit.rest.core.parsing.Invocation;

/**
 * Builder object for REST application.
 *
 * @author Andrey Minov
 */
public class ApplicationBuilder {
  static {
    RuntimeDelegate.setInstance(new RestRuntimeDelegate());
  }

  private int port;
  private String matchingUrls;
  private Collection<Class<?>> services;
  private Collection<Object> servicesInstances;
  private ConvertersStore convertersStore;

  private ApplicationBuilder() {
  }

  /**
   * Create new REST application builder.
   *
   * @return the new instance of REST application builder.
   */
  public static ApplicationBuilder builder() {
    return new ApplicationBuilder();
  }

  /**
   * Build REST application with resources matching urls path.
   * For example setting this to '/resources/*'
   * will lead all application resources started with '/service' string.
   *
   * @param urls the urls matching patter.
   * @return the application builder instance.
   */
  public ApplicationBuilder withMatchingUrls(String urls) {
    this.matchingUrls = urls;
    return this;
  }

  /**
   * Adds new REST service to application.
   *
   * @param service the service class to add.
   * @return the application builder instance.
   */
  public ApplicationBuilder withService(Class<?> service) {
    checkNotNull(service, "Service cannot be null!");
    if (services == null) {
      services = new ArrayList<>();
    }
    services.add(service);
    return this;
  }

  /**
   * Adds new REST service to application.
   *
   * @param service the service class to add.
   * @return the application builder instance.
   */
  public ApplicationBuilder withServiceInstance(Object service) {
    checkNotNull(service, "Service cannot be null!");
    if (servicesInstances == null) {
      servicesInstances = new ArrayList<>();
    }
    servicesInstances.add(service);
    return this;
  }

  /**
   * Set rest application HTTP port to value provided.
   *
   * @param port the HTTP port of application.
   * @return the application builder
   */
  public ApplicationBuilder withPort(int port) {
    this.port = port;
    return this;
  }

  /**
   * Register incoming parameter converter.
   *
   * @param <T>     the type of parameter
   * @param clazz   the clazz of parameter
   * @param convert the convert function from string to defined type.
   * @return the application builder instance.
   * @throws NullPointerException in case convert function or class is null.
   */
  public <T> ApplicationBuilder withInConverter(Class<T> clazz, Function<String, T> convert) {
    checkNotNull(convert, "Convert function cannot be null!");
    checkNotNull(clazz, "Clazz cannot be null!");

    if (convertersStore == null) {
      convertersStore = new ConvertersStore();
    }
    convertersStore.registerInConverter(clazz, convert);
    return this;
  }

  /**
   * Register writer of response object into servlet output stream.
   *
   * @param <T>    the type parameter to register
   * @param clazz  the clazz of the service response
   * @param writer the writer of which consumer response and object to write into response.
   * @return the application builder instance
   * @throws NullPointerException in case writer or clazz is null.
   */
  public <T> ApplicationBuilder withOutWriter(Class<T> clazz,
                                              BiConsumer<HttpServletResponse, T> writer) {
    checkNotNull(writer, "Writer function cannot be null!");
    checkNotNull(clazz, "Clazz cannot be null!");

    if (convertersStore == null) {
      convertersStore = new ConvertersStore();
    }
    convertersStore.registerOutWriter(clazz, writer);
    return this;
  }

  /**
   * Build new instance of {@link RestApplication}.
   *
   * @param objectSupplier the service objects supplier
   * @param serviceParser  the service metadata parser
   * @return the REST application instance
   * @throws IllegalArgumentException when matching urls is empty or port is our of range.
   * @throws NullPointerException     in case objectSupplier function or serviceParser is null
   */
  public RestApplication build(Function<Class<?>, Object> objectSupplier,
                               BiFunction<ConvertersStore, Class<?>,
                               List<Invocation>> serviceParser) {
    checkArgument(!isNullOrEmpty(matchingUrls), "Matching URLs must not be empty!");
    checkArgument(port > 0 && port < Character.MAX_VALUE, "Application port is not correct!");

    checkNotNull(objectSupplier, "Object supplier cannot be null!");
    checkNotNull(serviceParser, "Service parser cannot be null!");

    if (convertersStore == null) {
      convertersStore = new ConvertersStore();
    }

    ServicesStore servicesStore = new ServicesStore(convertersStore, objectSupplier, serviceParser);
    if (services != null) {
      services.forEach(servicesStore::addService);
    }
    if (servicesInstances != null) {
      servicesInstances.forEach(servicesStore::addServiceInstance);
    }

    return new RestApplication(port, matchingUrls, servicesStore);
  }


}
