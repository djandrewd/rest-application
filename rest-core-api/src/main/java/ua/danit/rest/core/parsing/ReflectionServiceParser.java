package ua.danit.rest.core.parsing;

import java.util.List;

import ua.danit.rest.core.convertors.ConvertersStore;

/**
 * Parsing of the service metadata using reflection.
 *
 * @author Andrey Minov
 */
public class ReflectionServiceParser {

  /**
   * Parse service metadata into list of invocations.
   *
   * @param convertersStore the storage for convertors
   * @param serviceClazz    the service clazz
   * @return the list of service invocations of {@link Invocation}
   */
  public List<Invocation> parse(ConvertersStore convertersStore, Class<?> serviceClazz) {
    throw new UnsupportedOperationException();
  }
}
