package ua.danit.rest.weatherapp.test;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.danit.rest.core.ApplicationBuilder.builder;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import ua.danit.rest.core.RestApplication;
import ua.danit.rest.core.parsing.ReflectionServiceParser;
import ua.danit.rest.weatherapp.entity.Measure;
import ua.danit.rest.weatherapp.entity.WeatherCode;
import ua.danit.rest.weatherapp.ext.MeasurementService;
import ua.danit.rest.weatherapp.resources.WeatherSelectResource;
import ua.danit.rest.weatherapp.resources.WeatherStoreResource;

/**
 * Tests for matching weather API during implementation.
 *
 * @author Andrey Minov
 */
@Ignore
public class WeatherApiTest {

  private static final String SERVICE_URLS = "/resources/*";
  private static final int SERVICE_PORT = 3434;
  private static final int HTTP_TIMEOUT = 1000;
  private static final String SERVER_URI_PATTERN = "http://localhost:%1$d%2$s";

  private static RestApplication application;
  private static MeasurementService measurementService;
  private static CloseableHttpClient httpClient;

  @BeforeClass
  public static void initApplication() throws Exception {
    measurementService = mock(MeasurementService.class);
    ReflectionServiceParser parser = new ReflectionServiceParser();
    application = builder().withPort(SERVICE_PORT).withMatchingUrls(SERVICE_URLS)
                           .withServiceInstance(new WeatherSelectResource(measurementService))
                           .withServiceInstance(new WeatherStoreResource(measurementService))
                           .build(Mockito::mock, parser::parse);
    application.start(false);
    httpClient = HttpClientBuilder.create().build();
  }


  @BeforeClass
  public static void closeApplication() throws Exception {
    if (application != null) {
      application.stop();
    }
    if (httpClient != null) {
      httpClient.close();
    }
  }

  private static Response callGet(String uri, Map<String, String> parameters) throws Exception {
    URIBuilder builder = new URIBuilder(String.format(SERVER_URI_PATTERN, SERVICE_PORT, uri));
    if (parameters != null) {
      parameters.forEach(builder::addParameter);
    }
    HttpGet get = new HttpGet(builder.build());
    get.setConfig(RequestConfig.custom().setConnectTimeout(HTTP_TIMEOUT).build());
    try (CloseableHttpResponse response = httpClient.execute(get)) {
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != SC_OK) {
        return new Response(statusCode, null);
      }
      byte[] bytes = ByteStreams.toByteArray(response.getEntity().getContent());
      return new Response(statusCode, new String(bytes, Charset.forName("UTF-8")));
    }
  }

  private static Response callPost(String uri, Map<String, String> parameters,
                                   String entity) throws Exception {
    URIBuilder builder = new URIBuilder(String.format(SERVER_URI_PATTERN, SERVICE_PORT, uri));
    if (parameters != null) {
      parameters.forEach(builder::addParameter);
    }
    HttpPost post = new HttpPost(builder.build());
    post.setEntity(new StringEntity(entity));
    post.setConfig(RequestConfig.custom().setConnectTimeout(HTTP_TIMEOUT).build());
    try (CloseableHttpResponse response = httpClient.execute(post)) {
      return new Response(response.getStatusLine().getStatusCode(), null);
    }
  }

  @Test
  public void testNotFoundOnNotExistedService() throws Exception {
    Response response = callGet("/resources/cars", Collections.emptyMap());
    assertEquals("Not correct status code!", SC_NOT_FOUND, response.getCode());
  }

  @Test
  public void testCityParameterMissing() throws Exception {
    // Call must be made for /resources/weather/byCity
    Response response = callGet("/resources/weather/get/byCity", ImmutableMap
        .of("country", "Ukraine"));
    assertEquals("Not correct status code!", SC_BAD_REQUEST, response.getCode());
  }

  @Test
  public void testCountryParameterMissing() throws Exception {
    // Call must be made for /resources/weather/byCity
    Response response = callGet("/resources/weather/get/byCity", ImmutableMap.of("city", "Kiev"));
    assertEquals("Not correct status code!", SC_BAD_REQUEST, response.getCode());
  }

  @Test
  public void testLongitudeParamMissing() throws Exception {
    // Call must be made for /resources/weather/byLocation
    Response response = callGet("/resources/weather/get/byLocation", ImmutableMap
        .of("latitude", "51.0"));
    assertEquals("Not correct status code!", SC_BAD_REQUEST, response.getCode());
  }

  @Test
  public void testLatutudeParamMissing() throws Exception {
    // Call must be made for /resources/weather/byLocation
    Response response = callGet("/resources/weather/get/byLocation", ImmutableMap
        .of("longitude", "31.0"));
    assertEquals("Not correct status code!", SC_BAD_REQUEST, response.getCode());
  }

  @Test
  public void testCountryParamMissedOnSave() throws Exception {
    String measurement = "{\"city\":\"Kiev\",\"weatherCode\":\"CLOUDY\","
                         + "\"temperature\":7.0,\"measureTime\":\"2017-11-11T10:00:00\"}";

    // Call must be made for /resources/weather/byCity
    Response response = callPost("/resources/weather/submit/measurement", Collections
        .emptyMap(), measurement);
    assertEquals("Not correct status code!", SC_BAD_REQUEST, response.getCode());
  }

  @Test
  public void testLocationParamMissedOnSave() throws Exception {
    String measurement = "{\"weatherCode\":\"CLOUDY\","
                         + "\"temperature\":7.0,\"measureTime\":\"2017-11-11T10:00:00\"}";

    // Call must be made for /resources/weather/byCity
    Response response = callPost("/resources/weather/submit/measurement", Collections
        .emptyMap(), measurement);
    assertEquals("Not correct status code!", SC_BAD_REQUEST, response.getCode());
  }


  @Test
  public void testCityParamMissedOnSave() throws Exception {
    String measurement = "{\"country\":\"Ukraine\",\"weatherCode\":\"CLOUDY\","
                         + "\"temperature\":7.0,\"measureTime\":\"2017-11-11T10:00:00\"}";

    // Call must be made for /resources/weather/byCity
    Response response = callPost("/resources/weather/submit/measurement", Collections
        .emptyMap(), measurement);
    assertEquals("Not correct status code!", SC_BAD_REQUEST, response.getCode());
  }

  @Test
  public void testGetKievMeasument() throws Exception {
    Measure measure = new Measure(10.0, WeatherCode.SUNNY, LocalDateTime
        .parse("2017-01-01T00:00:00").atZone(ZoneOffset.UTC));
    when(measurementService.getCurrentWeather("Kiev", "Ukraine")).thenReturn(measure);

    // Call must be made for /resources/weather/byCity
    Response response = callGet("/resources/weather/get/byCity", ImmutableMap
        .of("city", "Kiev", "country", "Ukraine"));
    assertEquals("Not correct status code!", SC_OK, response.getCode());
    assertEquals("{\"city\":\"Kiev\",\"country\":\"Ukraine\",\"weatherCode\":\"SUNNY\","
                 + "\"temperature\":10.0,\"measureTime\":\"2017-01-01T00:00:00\"}", response
        .getResponse());
  }

  @Test
  public void testGetSomeLocationMeasument() throws Exception {
    Measure measure = new Measure(15.0, WeatherCode.CLOUDY, LocalDateTime
        .parse("2017-05-12T00:00:00").atZone(ZoneOffset.UTC));
    when(measurementService.getCurrentWeather(31.0, 51.0)).thenReturn(measure);

    // Call must be made for /resources/weather/byLocation
    Response response = callGet("/resources/weather/get/byLocation", ImmutableMap
        .of("longitude", "31.0", "latitude", "51.0"));
    assertEquals("Not correct status code!", SC_OK, response.getCode());
    assertEquals("{\"location\":{\"longitude\":31.0,\"latitude\":51.0},"
                 + "\"weatherCode\":\"CLOUDY\",\"temperature\":15.0,"
                 + "\"measureTime\":\"2017-05-12T00:00:00\"}", response.getResponse());
  }


  @Test
  public void testSaveKievMeasument() throws Exception {
    String measurement = "{\"city\":\"Kiev\",\"country\":\"Ukraine\",\"weatherCode\":\"CLOUDY\","
                         + "\"temperature\":7.0,\"measureTime\":\"2017-11-11T10:00:00\"}";

    // Call must be made for /resources/weather/byCity
    Response response = callPost("/resources/weather/submit/measurement", Collections
        .emptyMap(), measurement);
    assertEquals("Not correct status code!", SC_OK, response.getCode());
    verify(measurementService, times(1))
        .storeMeasure("Kiev", "Ukraine", new Measure(7.0, WeatherCode.CLOUDY, LocalDateTime
            .parse("2017-11-11T10:00:00").atZone(ZoneOffset.UTC)));
  }

  @Test
  public void testSaveLocationMeasument() throws Exception {
    String measurement =
        "{\"location\":{\"longitude\":24.0,\"latitude\":67.0},\"weatherCode\":\"HEAVY_RAIN\","
        + "\"temperature\":17.0,\"measureTime\":\"2016-03-31T10:00:00\"}";

    // Call must be made for /resources/weather/byCity
    Response response = callPost("/resources/weather/submit/measurement", Collections
        .emptyMap(), measurement);
    assertEquals("Not correct status code!", SC_OK, response.getCode());
    verify(measurementService, times(1))
        .storeMeasure(24.0, 67.0, new Measure(17.0, WeatherCode.HEAVY_RAIN, LocalDateTime
            .parse("2016-03-31T10:00:00").atZone(ZoneOffset.UTC)));
  }


  @Test
  public void testInternalError() throws Exception {
    when(measurementService.getCurrentWeather("Odessa", "Ukraine"))
        .thenThrow(new IllegalArgumentException());
    // Call must be made for /resources/weather/byCity
    Response response = callGet("/resources/weather/get/byCity", ImmutableMap
        .of("city", "Odessa", "country", "Ukraine"));
    assertEquals("Not correct status code!", SC_INTERNAL_SERVER_ERROR, response.getCode());
  }

  private static class Response {
    private int code;
    private String response;

    Response(int code, String response) {
      this.code = code;
      this.response = response;
    }

    int getCode() {
      return code;
    }

    String getResponse() {
      return response;
    }
  }


}
