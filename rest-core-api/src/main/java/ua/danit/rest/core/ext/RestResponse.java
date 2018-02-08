package ua.danit.rest.core.ext;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

/**
 * Implementation for {@link javax.xml.ws.Response} object.
 *
 * @author Andrey Minov
 */
public class RestResponse extends Response {

  private int status;
  private Object entity;
  private Set<String> httpMethods;
  private MultivaluedMap<String, Object> headers;
  private CacheControl cacheControl;
  private MediaType mediaType;
  private String encoding;
  private Collection<NewCookie> cookies;

  /**
   * Instantiates a new Rest response.
   *
   * @param status       the HTTP status code.
   * @param entity       the entity stored inside.
   * @param httpMethods  the http methods allowed for this response
   * @param headers      the HTTP headers
   * @param cacheControl the cache control
   * @param mediaType    the media type of the response
   * @param encoding     the encoding of the response
   * @param cookies      the cookies
   */
  public RestResponse(int status, Object entity, Set<String> httpMethods,
                      MultivaluedMap<String, Object> headers, CacheControl cacheControl,
                      MediaType mediaType, String encoding, Collection<NewCookie> cookies) {
    this.status = status;
    this.entity = entity;
    this.httpMethods = httpMethods;
    this.headers = headers;
    this.cacheControl = cacheControl;
    this.mediaType = mediaType;
    this.encoding = encoding;
    this.cookies = cookies;
  }

  @Override
  public int getStatus() {
    return status;
  }

  @Override
  public Object getEntity() {
    return entity;
  }

  @Override
  public MultivaluedMap<String, Object> getHeaders() {
    return headers;
  }

  @Override
  public MediaType getMediaType() {
    return mediaType;
  }

  public CacheControl getCacheControl() {
    return cacheControl;
  }

  public String getEncoding() {
    return encoding;
  }

  @Override
  public StatusType getStatusInfo() {
    return Status.fromStatusCode(status);
  }

  @Override
  public <T> T readEntity(Class<T> entityType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T readEntity(GenericType<T> entityType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
    return null;
  }

  @Override
  public boolean hasEntity() {
    return entity != null;
  }

  @Override
  public boolean bufferEntity() {
    return false;
  }

  @Override
  public void close() {
  }

  @Override
  public Locale getLanguage() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getLength() {
    return 0;
  }

  @Override
  public Set<String> getAllowedMethods() {
    return httpMethods;
  }

  @Override
  public Map<String, NewCookie> getCookies() {
    if (cookies == null) {
      return Collections.emptyMap();
    }
    return cookies.stream().collect(Collectors.toMap(NewCookie::getName, Function.identity()));
  }

  @Override
  public EntityTag getEntityTag() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Date getDate() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Date getLastModified() {
    throw new UnsupportedOperationException();
  }

  @Override
  public URI getLocation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<Link> getLinks() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasLink(String relation) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Link getLink(String relation) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Link.Builder getLinkBuilder(String relation) {
    return null;
  }

  @Override
  public MultivaluedMap<String, Object> getMetadata() {
    throw new UnsupportedOperationException();
  }

  @Override
  public MultivaluedMap<String, String> getStringHeaders() {
    MultivaluedMap<String, String> response = new MultivaluedHashMap<>();
    if (headers != null) {
      headers.forEach((name, value) -> value
          .forEach(hValue -> response.putSingle(name, String.valueOf(hValue))));
    }
    return response;
  }

  @Override
  public String getHeaderString(String name) {
    if (headers == null) {
      return null;
    }
    return String.valueOf(headers.getFirst(name));
  }
}
