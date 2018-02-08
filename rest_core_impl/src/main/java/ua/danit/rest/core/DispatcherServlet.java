package ua.danit.rest.core;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static ua.danit.rest.core.utils.Reflections.fillParameters;

import com.google.common.base.Strings;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.executable.ExecutableValidator;

import ua.danit.rest.core.parsing.Invocation;
import ua.danit.rest.core.parsing.MethodType;

/**
 * Dispatcher servlet for handing all HTTP requests for some path and delegate
 * execution to resources.
 *
 * @author Andrey Minov
 */
public class DispatcherServlet extends HttpServlet {

  private static final String MEDIA_TYPE_HEADER = "Content-Type";
  // from javadoc:  Implementations of this interface must be thread-safe.
  private static final ExecutableValidator VALIDATOR =
      Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  private ServicesStore servicesStore;

  /**
   * Instantiates a new Dispatcher servlet.
   *
   * @param servicesStore the resources store
   */
  DispatcherServlet(ServicesStore servicesStore) {
    this.servicesStore = servicesStore;
  }

  @Override
  protected void doGet(HttpServletRequest req,
                       HttpServletResponse resp) throws ServletException, IOException {
    doHttpCall(req, resp, MethodType.GET);
  }

  @Override
  protected void doPost(HttpServletRequest req,
                        HttpServletResponse resp) throws ServletException, IOException {
    doHttpCall(req, resp, MethodType.POST);
  }

  private void doHttpCall(HttpServletRequest req, HttpServletResponse resp,
                          MethodType methodType) throws IOException {
    String path = req.getRequestURI();
    String servletUri = req.getServletPath();
    String uri = path.replaceAll(servletUri, "");
    if (Strings.isNullOrEmpty(uri)) {
      resp.setStatus(SC_NOT_FOUND);
      return;
    }
    Invocation invocation = servicesStore.getServiceMeta(methodType, uri);
    if (invocation == null) {
      resp.sendError(SC_NOT_FOUND);
      return;
    }
    try {
      Object[] params = fillParameters(invocation, req);
      Method method = invocation.getMethod();
      Object instance = servicesStore.getServiceInstance(method.getDeclaringClass());

      Set<ConstraintViolation<Object>> errors =
          VALIDATOR.validateParameters(instance, method, params);
      if (errors != null && !errors.isEmpty()) {
        ConstraintViolation<Object> violation = errors.iterator().next();
        resp.sendError(SC_BAD_REQUEST, violation.getMessage());
        return;
      }

      if (!Strings.isNullOrEmpty(invocation.getMediaType())) {
        resp.setHeader(MEDIA_TYPE_HEADER, invocation.getMediaType());
      }
      invocation.getResponseWriter().accept(resp, method.invoke(instance, params));
    } catch (Exception e) {
      resp.sendError(SC_INTERNAL_SERVER_ERROR);
    }
  }
}
