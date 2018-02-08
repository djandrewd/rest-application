package ua.danit.rest.core.convertors;

import static com.google.common.io.ByteStreams.toByteArray;
import static java.util.Optional.ofNullable;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Storage used for convert string parameters into object types and visa versa.
 * <p/>
 * Support basic conventions from scratch: string, primitives.
 *
 * @author Andrey Minov
 */
public class ConvertersStore {

  private final Map<Class<?>, Function<String, ?>> inConverters;
  private final Map<Class<?>, BiConsumer<HttpServletResponse, ?>> outWriters;
  private final Map<String, BiFunction<InputStream, Class<?>, ?>> mediaTypeInConverters;
  private final Map<String, BiConsumer<?, ServletOutputStream>> mediaTypeOutWriters;
  private final BiConsumer<HttpServletResponse, Object> defaultWriter;
  private final Gson gson;

  /**
   * Instantiates a new Converters store.
   */
  public ConvertersStore() {
    this.inConverters = new ConcurrentHashMap<>();
    this.outWriters = new ConcurrentHashMap<>();
    this.mediaTypeInConverters = new ConcurrentHashMap<>();
    this.mediaTypeOutWriters = new ConcurrentHashMap<>();
    this.gson = new Gson();
    this.defaultWriter = createDefaultWriter();
    registerBuildInConverters();
  }

  private static void printAndFlush(ServletOutputStream stream, String data) {
    try {
      stream.print(data);
      stream.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void printAndFlush(ServletOutputStream stream, byte[] data) {
    try {
      stream.write(data);
      stream.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String streamToString(InputStream inputStream) {
    return new String(streamToBytes(inputStream), Charset.forName("UTF-8"));
  }

  private static byte[] streamToBytes(InputStream inputStream) {
    try {
      return toByteArray(inputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void registerBuildInConverters() {
    inConverters.put(int.class, v -> v != null ? Integer.parseInt(v) : null);
    inConverters.put(Integer.class, v -> v != null ? Integer.parseInt(v) : null);
    inConverters.put(double.class, v -> v != null ? Double.parseDouble(v) : null);
    inConverters.put(Double.class, v -> v != null ? Double.parseDouble(v) : null);
    inConverters.put(long.class, v -> v != null ? Long.parseLong(v) : null);
    inConverters.put(Long.class, v -> v != null ? Long.parseLong(v) : null);
    inConverters.put(float.class, v -> v != null ? Float.parseFloat(v) : null);
    inConverters.put(Float.class, v -> v != null ? Float.parseFloat(v) : null);
    inConverters.put(byte.class, v -> v != null ? Byte.parseByte(v) : null);
    inConverters.put(Byte.class, v -> v != null ? Byte.parseByte(v) : null);
    inConverters.put(boolean.class, v -> v != null ? Boolean.parseBoolean(v) : null);
    inConverters.put(Boolean.class, v -> v != null ? Boolean.parseBoolean(v) : null);
    inConverters.put(String.class, Function.identity());

    outWriters.put(Response.class, fromRsResponseWriter());

    mediaTypeInConverters
        .put(MediaType.APPLICATION_JSON, (v, c) -> gson.fromJson(streamToString(v), c));
    mediaTypeInConverters.put(MediaType.TEXT_PLAIN, (v, c) -> streamToString(v));
    mediaTypeInConverters.put(MediaType.APPLICATION_OCTET_STREAM, (v, c) -> streamToBytes(v));

    mediaTypeOutWriters.put(MediaType.APPLICATION_JSON, (v, str) -> ofNullable(v)
        .ifPresent(e -> printAndFlush(str, gson.toJson(e))));
    mediaTypeOutWriters
        .put(MediaType.TEXT_PLAIN, (v, str) -> printAndFlush(str, String.valueOf(v)));
    mediaTypeOutWriters
        .put(MediaType.APPLICATION_OCTET_STREAM, (v, str) -> printAndFlush(str, (byte[]) v));
  }

  /**
   * Register incoming parameter converter.
   *
   * @param <T>     the type parameter of argument.
   * @param clazz   the clazz to register
   * @param convert the convert function from string to object
   */
  public <T> void registerInConverter(Class<T> clazz, Function<String, T> convert) {
    inConverters.put(clazz, convert);
  }

  /**
   * Register out response writer.
   *
   * @param <T>      the type parameter of response.
   * @param clazz    the clazz to register
   * @param consumer consumes http response to transfer all the data to.
   */
  public <T> void registerOutWriter(Class<T> clazz, BiConsumer<HttpServletResponse, T> consumer) {
    outWriters.put(clazz, consumer);
  }

  /**
   * Gets media converter which converts body of provided media type to object.
   *
   * @param <T>       the type parameter of response
   * @param clazz     the clazz to convert to
   * @param mediaType the media type of the boby
   * @return converter from body stream to object using media type provided.
   */
  @SuppressWarnings("unchecked")
  public <T> Function<InputStream, T> getMediaInConverter(Class<T> clazz, String mediaType) {
    BiFunction<InputStream, Class<?>, ?> mediaConverter = mediaTypeInConverters.get(mediaType);
    if (mediaConverter != null) {
      return s -> (T) mediaConverter.apply(s, clazz);
    }
    return s -> null;
  }

  /**
   * Gets converter for incoming object by conversion from string.
   *
   * @param <T>   the type parameter
   * @param clazz the clazz
   * @return the in converter
   */
  @SuppressWarnings("unchecked")
  public <T> Function<String, T> getInConverter(Class<T> clazz) {
    Function<String, T> converter = (Function<String, T>) inConverters.get(clazz);
    if (converter != null) {
      return converter;
    }
    throw new IllegalArgumentException(String
        .format("Not existed in converter for class %s!", clazz));
  }

  /**
   * Gets consumer for response objects to write them into HTTP response object
   *
   * @param clazz the clazz type of parameter
   * @return consumer for response objects.
   * @throws IllegalArgumentException when no converter found in storage.
   */
  @SuppressWarnings("unchecked")
  public BiConsumer<HttpServletResponse, Object> getOutWriter(Class<?> clazz) {
    BiConsumer<HttpServletResponse, Object> converter =
        (BiConsumer<HttpServletResponse, Object>) outWriters
        .get(clazz);
    if (converter != null) {
      return converter;
    }
    return defaultWriter;
  }

  private BiConsumer<HttpServletResponse, Object> createDefaultWriter() {
    return (resp, entity) -> writeEntity(resp, entity, resp.getContentType());
  }

  private BiConsumer<HttpServletResponse, Object> fromRsResponseWriter() {
    return (resp, entity) -> {
      Response response = (Response) entity;
      resp.setStatus(response.getStatus());
      String contentType =
          response.getMediaType() != null ? response.getMediaType().getType() : resp
              .getContentType();
      if (response.hasEntity()) {
        writeEntity(resp, response.getEntity(), contentType);
      }
      if (response.getHeaders() != null) {
        response.getHeaders().forEach((name, value) -> {
          for (Object header : value) {
            resp.addHeader(name, String.valueOf(header));
          }
        });
      }
      if (response.getCookies() != null) {
        response.getCookies().forEach((name, cookie) -> resp
            .addCookie(new Cookie(cookie.getName(), cookie.getValue())));
      }
    };
  }

  private void writeEntity(HttpServletResponse response, Object entity, String contentType) {
    try {
      getEntityConverter(contentType).accept(entity, response.getOutputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private BiConsumer<Object, ServletOutputStream> getEntityConverter(String mediaType) {
    if (!Strings.isNullOrEmpty(mediaType)) {
      BiConsumer<Object, ServletOutputStream> consumer =
          (BiConsumer<Object, ServletOutputStream>) mediaTypeOutWriters
          .get(mediaType);
      if (consumer != null) {
        return consumer;
      }
    }
    return (value, stream) -> printAndFlush(stream, String.valueOf(value));
  }
}
