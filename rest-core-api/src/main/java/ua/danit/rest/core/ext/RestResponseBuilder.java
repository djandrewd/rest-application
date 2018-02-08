package ua.danit.rest.core.ext;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

import com.google.common.base.Strings;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

/**
 * Builder for {@link Response} objects.
 *
 * @author Andrey Minov
 */
class RestResponseBuilder extends Response.ResponseBuilder {

  private int status = Response.Status.OK.getStatusCode();
  private Object entity;
  private Set<String> httpMethods;
  private MultivaluedMap<String, Object> headers;
  private CacheControl cacheControl;
  private MediaType mediaType;
  private String encoding;
  private Collection<NewCookie> cookies;

  @Override
  public Response build() {
    return new RestResponse(status, entity, httpMethods, headers, cacheControl, mediaType, encoding,
        cookies);
  }

  @Override
  public Response.ResponseBuilder clone() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder status(int status) {
    this.status = status;
    return this;
  }

  @Override
  public Response.ResponseBuilder entity(Object entity) {
    this.entity = entity;
    return this;
  }

  @Override
  public Response.ResponseBuilder entity(Object entity, Annotation[] annotations) {
    return entity(entity);
  }

  @Override
  public Response.ResponseBuilder allow(String... methods) {
    return allow(new HashSet<>(asList(methods)));
  }

  @Override
  public Response.ResponseBuilder allow(Set<String> methods) {
    checkNotNull(methods, "Methods cannot be null!");
    this.httpMethods = methods;
    return this;
  }

  @Override
  public Response.ResponseBuilder cacheControl(CacheControl cacheControl) {
    this.cacheControl = cacheControl;
    return this;
  }

  @Override
  public Response.ResponseBuilder encoding(String encoding) {
    this.encoding = encoding;
    return this;
  }

  @Override
  public Response.ResponseBuilder header(String name, Object value) {
    checkArgument(!Strings.isNullOrEmpty(name), "Header name cannot be empty!");
    checkNotNull(value, "Header value cannot be null!");
    if (headers == null) {
      headers = new MultivaluedHashMap<>();
    }
    headers.putSingle(name, value);
    return this;
  }

  @Override
  public Response.ResponseBuilder replaceAll(MultivaluedMap<String, Object> newHeaders) {
    checkNotNull(newHeaders, "Header value cannot be null!");
    if (headers == null) {
      headers = new MultivaluedHashMap<>();
    }
    headers.putAll(newHeaders);
    return this;
  }

  @Override
  public Response.ResponseBuilder language(String language) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder language(Locale language) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder type(MediaType type) {
    checkNotNull(type, "%s value cannot be null!", "Type");
    this.mediaType = type;
    return this;
  }

  @Override
  public Response.ResponseBuilder type(String type) {
    checkArgument(!Strings.isNullOrEmpty(type), "$s cannot be empty!", "Type");
    this.mediaType = MediaType.valueOf(type);
    return this;
  }

  @Override
  public Response.ResponseBuilder variant(Variant variant) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder contentLocation(URI location) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder cookie(NewCookie... cookies) {
    this.cookies = Arrays.asList(cookies);
    return this;
  }

  @Override
  public Response.ResponseBuilder expires(Date expires) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder lastModified(Date lastModified) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder location(URI location) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder tag(EntityTag tag) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder tag(String tag) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder variants(Variant... variants) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder variants(List<Variant> variants) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder links(Link... links) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder link(URI uri, String rel) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder link(String uri, String rel) {
    throw new UnsupportedOperationException();
  }
}
