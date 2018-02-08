package ua.danit.rest.core.parsing;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Presented invocation that must be called when REST method must be called.
 *
 * @author Andrey Minov
 */
public class Invocation {
  private String url;
  private Method method;
  private Map<Integer, Parameter> params;
  private int paramCount;
  private MethodType methodType;
  private String mediaType;
  private BiConsumer<HttpServletResponse, Object> responseWriter;

  /**
   * Instantiates a new Invocation.
   *
   * @param url            the url on which to register invocation. For example /weather
   * @param method         the method that must be called when calling service
   * @param params         the params to pass to methods above and ones that
   *                       must be get from request.
   * @param paramCount     the number of method call parameters
   * @param methodType     the HTTP method type invocation is bound to.
   * @param mediaType      of response body
   * @param responseWriter the response writer from entity to HTTP response entity
   */
  Invocation(String url, Method method, Map<Integer, Parameter> params, int paramCount,
             MethodType methodType, String mediaType,
             BiConsumer<HttpServletResponse, Object> responseWriter) {
    this.url = url;
    this.method = method;
    this.params = params;
    this.paramCount = paramCount;
    this.methodType = methodType;
    this.mediaType = mediaType;
    this.responseWriter = responseWriter;
  }

  public MethodType getMethodType() {
    return methodType;
  }

  public String getUrl() {
    return url;
  }

  public Method getMethod() {
    return method;
  }

  public Map<Integer, Parameter> getParams() {
    return params;
  }

  public int getParamCount() {
    return paramCount;
  }

  public BiConsumer<HttpServletResponse, Object> getResponseWriter() {
    return responseWriter;
  }

  public String getMediaType() {
    return mediaType;
  }

  /**
   * Presents type of method call parameter.
   *
   * @author Andrey Minov
   */
  public static class Parameter {
    private java.lang.reflect.Parameter parameter;
    private Function<HttpServletRequest, ?> converter;

    /**
     * Instantiates a new Parameter.
     *
     * @param converter the converter for parameter value
     * @param parameter the java parameter for this field.
     */
    Parameter(Function<HttpServletRequest, ?> converter, java.lang.reflect.Parameter parameter) {
      this.converter = converter;
      this.parameter = parameter;
    }

    public Function<HttpServletRequest, ?> getConverter() {
      return converter;
    }

    public java.lang.reflect.Parameter getParameter() {
      return parameter;
    }
  }
}
