package ua.danit.rest.core.utils;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import ua.danit.rest.core.parsing.Invocation;

/**
 * Utility methods for using reflections.
 *
 * @author Andrey Minov
 */
public class Reflections {


  /**
   * Convert HTTP request into array of parameters of service method call.
   *
   * @param invocation the invocation
   * @param request    the HTTP request request
   * @return array of object used in {@link java.lang.reflect.Method} invoke method as parameters.
   */
  public static Object[] fillParameters(Invocation invocation, HttpServletRequest request) {
    Object[] params = new Object[invocation.getParamCount()];
    for (Map.Entry<Integer, Invocation.Parameter> reqParam : invocation.getParams().entrySet()) {
      Invocation.Parameter parameter = reqParam.getValue();
      Object param = parameter.getConverter().apply(request);
      params[reqParam.getKey()] = param;
    }
    return params;
  }

}
