package ua.danit.rest.core;

import java.util.function.Function;

/**
 * Supplier for the resources instance from class instance
 * using reflection and assuming default constructor exists.
 *
 * @author Andrey Minov
 */
public class ReflectionServiceSupplier implements Function<Class<?>, Object> {

  @Override
  public Object apply(Class<?> argClass) {
    try {
      // default constructor is cached already.
      return argClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
