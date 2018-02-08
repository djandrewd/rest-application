package ua.danit.rest.core.parsing;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import ua.danit.rest.core.convertors.ConvertersStore;

/**
 * Parse servlet presentation into {@link Invocation} structure.
 *
 * @author Andrey Minov
 */
public class RealReflectionServiceParser extends ReflectionServiceParser {

  private static MethodType getMethodType(Method method) {
    if (method.isAnnotationPresent(GET.class)) {
      return MethodType.GET;
    }
    if (method.isAnnotationPresent(POST.class)) {
      return MethodType.POST;
    }
    return null;
  }

  private static String getMethodUrl(Class<?> clazz, Method method) {
    StringBuilder uri = new StringBuilder();
    if (clazz.isAnnotationPresent(Path.class)) {
      String path = clazz.getAnnotation(Path.class).value();
      if (!path.startsWith("/")) {
        uri.append("/");
      }
      uri.append(path);
    }
    if (method.isAnnotationPresent(Path.class)) {
      String path = method.getAnnotation(Path.class).value();
      if (!path.startsWith("/")) {
        uri.append("/");
      }
      uri.append(path);
    } else {
      uri.append("/");
      uri.append(method.getName());
    }
    return uri.toString();
  }

  private static Function<HttpServletRequest, ?> getParamConverter(Parameter parameter,
                                                                   ConvertersStore convertersStore,
                                                                   MethodType methodType,
                                                                   String mediaType) {
    if (parameter.isAnnotationPresent(Context.class)) {
      return Function.identity();
    }
    if (parameter.isAnnotationPresent(QueryParam.class)) {
      return getQueryParamConverter(parameter, convertersStore.getInConverter(parameter.getType()));
    }
    if (parameter.isAnnotationPresent(HeaderParam.class)) {
      return getHeaderParamConverter(parameter,
          convertersStore.getInConverter(parameter.getType()));
    }
    if (parameter.isAnnotationPresent(PathParam.class)) {
      throw new UnsupportedOperationException("Path param is not supported yet!");
    }
    if (parameter.isAnnotationPresent(FormParam.class)) {
      throw new UnsupportedOperationException("Multipart param is not supported yet!");
    }
    if (parameter.isAnnotationPresent(CookieParam.class)) {
      return getCookieParamConverter(parameter,
          convertersStore.getInConverter(parameter.getType()));
    }
    if (!methodType.isSupportBody()) {
      return r -> parameter.isNamePresent() ? r.getParameter(parameter.getName()) : null;
    }
    // Otherwise parameter will be body specific
    return r -> {
      Function<InputStream, ?> mediaConverter =
          convertersStore.getMediaInConverter(parameter.getType(), mediaType);
      try {
        return mediaConverter.apply(r.getInputStream());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  private static String getInMediaType(Class<?> clazz, Method method) {
    String type = MediaType.TEXT_PLAIN;
    if (clazz.isAnnotationPresent(Consumes.class)) {
      type = clazz.getAnnotation(Consumes.class).value()[0];
    }
    if (method.isAnnotationPresent(Consumes.class)) {
      type = method.getAnnotation(Consumes.class).value()[0];
    }
    return type;
  }

  private static String getOutMediaType(Class<?> clazz, Method method) {
    String type = MediaType.TEXT_PLAIN;
    if (clazz.isAnnotationPresent(Produces.class)) {
      type = clazz.getAnnotation(Produces.class).value()[0];
    }
    if (method.isAnnotationPresent(Produces.class)) {
      type = method.getAnnotation(Produces.class).value()[0];
    }
    return type;
  }

  private static Function<HttpServletRequest, ?> getQueryParamConverter(Parameter parameter,
                                                                 Function<String, ?> inConverter) {
    String defValue = parameter.isAnnotationPresent(DefaultValue.class) ? parameter
        .getAnnotation(DefaultValue.class).value() : null;
    String name = parameter.getAnnotation(QueryParam.class).value();
    return r -> inConverter.apply(ofNullable(r.getParameter(name)).orElse(defValue));
  }

  private static Function<HttpServletRequest, ?> getHeaderParamConverter(Parameter parameter,
                                                                 Function<String, ?> inConverter) {
    String defValue = parameter.isAnnotationPresent(DefaultValue.class) ? parameter
        .getAnnotation(DefaultValue.class).value() : null;
    String name = parameter.getAnnotation(HeaderParam.class).value();
    return r -> inConverter.apply(ofNullable(r.getHeader(name)).orElse(defValue));
  }

  private static Function<HttpServletRequest, ?> getCookieParamConverter(Parameter parameter,
                                                                 Function<String, ?> inConverter) {
    String defValue = parameter.isAnnotationPresent(DefaultValue.class) ? parameter
        .getAnnotation(DefaultValue.class).value() : null;
    String name = parameter.getAnnotation(CookieParam.class).value();
    return r -> {
      Cookie cookie =
          stream(r.getCookies()).filter(c -> name.equals(c.getName())).findFirst().orElse(null);
      return inConverter.apply(ofNullable(cookie).map(Cookie::getValue).orElse(defValue));
    };
  }

  @Override
  public List<Invocation> parse(ConvertersStore convertersStore, Class<?> serviceClazz) {
    List<Invocation> invocations = new ArrayList<>();
    for (Method method : serviceClazz.getMethods()) {
      MethodType methodType = getMethodType(method);
      if (methodType == null) {
        continue;
      }
      String url = getMethodUrl(serviceClazz, method);
      Map<Integer, Invocation.Parameter> params = new HashMap<>();

      String inMediaType = getInMediaType(serviceClazz, method);
      String outMediaType = getOutMediaType(serviceClazz, method);

      for (int i = 0; i < method.getParameterCount(); i++) {
        Parameter parameter = method.getParameters()[i];
        Function<HttpServletRequest, ?> converter =
            getParamConverter(parameter, convertersStore, methodType, inMediaType);
        params.put(i, new Invocation.Parameter(converter, parameter));
      }
      invocations.add(
          new Invocation(url, method, params, method.getParameterCount(), methodType, outMediaType,
              convertersStore.getOutWriter(method.getReturnType())));
    }
    return invocations;
  }
}
