package ua.danit.rest.core.convertors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ua.danit.rest.core.utils.Reflections.fillParameters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ua.danit.rest.core.ServicesStore;
import ua.danit.rest.core.parsing.Invocation;
import ua.danit.rest.core.parsing.MethodType;
import ua.danit.rest.core.parsing.RealReflectionServiceParser;

/**
 * Test for storage convertors.
 *
 * @author Andrey Minov
 */
public class ParsingMethodsTest {

  private HttpServletRequest request;
  private ServicesStore servicesStore;

  private static ServletInputStream fromString(String value) throws IOException {
    return fromBytes(value.getBytes(Charset.forName("UTF-8")));
  }

  private static ServletInputStream fromBytes(byte[] value) throws IOException {
    InputStream stream = new ByteArrayInputStream(value);
    ServletInputStream result = mock(ServletInputStream.class);
    when(result.read(any())).then(i -> stream.read(i.getArgument(0)));
    when(result.read()).then(i -> stream.read());
    return result;
  }

  @Before
  public void setUp() throws Exception {
    request = mock(HttpServletRequest.class);
    servicesStore = new ServicesStore(new ConvertersStore(), Mockito::mock, new RealReflectionServiceParser()::parse);
    servicesStore.addService(RunService.class);
  }

  @Test
  public void testDaysQueryParam() {
    when(request.getParameter("day")).thenReturn("10");
    when(request.getParameter("month")).thenReturn("1");
    when(request.getHeader("year")).thenReturn("2017");

    Invocation invocation = servicesStore.getServiceMeta(MethodType.GET, "/run/workouts");
    assertNotNull(invocation);

    Object[] parameters = fillParameters(invocation, request);
    assertArrayEquals(new Object[] {10, 1, 2017L}, parameters);
  }

  @Test
  public void testSubmitPaceWithBodyParam() throws IOException {
    when(request.getParameter("pace")).thenReturn("10");
    ServletInputStream stream = fromString("test");
    when(request.getInputStream()).thenReturn(stream);

    Invocation invocation = servicesStore.getServiceMeta(MethodType.POST, "/run/pace");
    assertNotNull(invocation);

    Object[] parameters = fillParameters(invocation, request);
    assertArrayEquals(new Object[] {10, "test"}, parameters);
  }

  @Test
  public void testJsonResponseObject() throws IOException {
    ServletInputStream stream = fromString("{\"city\":\"Kiev\", \"route\": [\"1\", \"2\", \"3\"]}");
    when(request.getInputStream()).thenReturn(stream);

    Invocation invocation = servicesStore.getServiceMeta(MethodType.POST, "/run/map");
    assertNotNull(invocation);

    Object[] parameters = fillParameters(invocation, request);
    assertArrayEquals(new Object[] {new RunService.MapEntry("Kiev", new String[] {"1", "2", "3"})}, parameters);
  }

  @Test
  public void testBinaryParameters() throws IOException {
    ServletInputStream stream = fromBytes(new byte[] {1, 2, 3});
    when(request.getInputStream()).thenReturn(stream);

    Invocation invocation = servicesStore.getServiceMeta(MethodType.POST, "/run/binary");
    assertNotNull(invocation);

    Object[] parameters = fillParameters(invocation, request);
    assertArrayEquals(new Object[] {new byte[] {1, 2, 3}}, parameters);
  }
}