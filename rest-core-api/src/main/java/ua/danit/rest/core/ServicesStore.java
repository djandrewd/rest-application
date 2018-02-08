package ua.danit.rest.core;

import static java.util.EnumSet.allOf;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import ua.danit.rest.core.convertors.ConvertersStore;
import ua.danit.rest.core.parsing.Invocation;
import ua.danit.rest.core.parsing.MethodType;

/**
 * Stores resources together with metadata for calling the methods.
 *
 * @author Andrey Minov
 */
public class ServicesStore {

  private final ConvertersStore convertersStore;
  private final Function<Class<?>, Object> objectSupplier;
  private final BiFunction<ConvertersStore, Class<?>, List<Invocation>> serviceParser;
  private final EnumMap<MethodType, Map<String, Invocation>> invocations;
  private final Map<Class<?>, Object> cachedServices;

  /**
   * Instantiates a new Services store.
   *
   * @param convertersStore the converters store
   * @param objectSupplier  the supplier for objects.
   * @param serviceParser   the service parser for parsing meta-information from service class
   *                        to Invocation object
   */
  public ServicesStore(ConvertersStore convertersStore, Function<Class<?>, Object> objectSupplier,
                       BiFunction<ConvertersStore, Class<?>, List<Invocation>> serviceParser) {
    this.convertersStore = convertersStore;
    this.objectSupplier = objectSupplier;
    this.serviceParser = serviceParser;
    this.invocations = new EnumMap<>(MethodType.class);
    this.cachedServices = new HashMap<>();
    invocations.putAll(allOf(MethodType.class).stream().collect(Collectors
        .toMap(Function.identity(), k -> new ConcurrentHashMap<>())));
  }

  /**
   * Add service together with metadata into the storage.
   *
   * @param serviceClass the service class to register
   */
  public void addService(Class<?> serviceClass) {
    for (Invocation invocation : serviceParser.apply(convertersStore, serviceClass)) {
      invocations.get(invocation.getMethodType()).put(invocation.getUrl(), invocation);
    }
  }

  /**
   * Add service together with metadata into the storage.
   *
   * @param serviceInstance the service to register
   */
  public void addServiceInstance(Object serviceInstance) {
    addService(serviceInstance.getClass());
    cachedServices.put(serviceInstance.getClass(), serviceInstance);
  }

  /**
   * Gets service metadata from storage.
   *
   * @param methodType the HTTP method type to call. One of {@link MethodType}
   * @param url        the url on which service is registered
   * @return the service invocation used for service to invoke or null if empty.
   */
  public Invocation getServiceMeta(MethodType methodType, String url) {
    return invocations.get(methodType).get(url);
  }

  /**
   * Gets service instance.
   *
   * @param clazz the clazz of the service.
   * @return the service instance stored or null if not registered.
   */
  public Object getServiceInstance(Class<?> clazz) {
    return cachedServices.computeIfAbsent(clazz, objectSupplier);
  }
}
