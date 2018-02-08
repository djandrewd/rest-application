package ua.danit.rest.weatherapp.impl.resources;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

import com.google.common.base.Strings;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ua.danit.rest.weatherapp.entity.Measure;
import ua.danit.rest.weatherapp.entity.Weather;
import ua.danit.rest.weatherapp.ext.MeasurementService;

/**
 * Service for storing information about weather.
 *
 * @author Andrey Minov
 */
@Path("/weather/submit")
public class WeatherStoreResource {

  private MeasurementService measurementService;

  /**
   * Instantiates a new Weather store service.
   *
   * @param measurementService the measurement service
   */
  public WeatherStoreResource(MeasurementService measurementService) {
    this.measurementService = measurementService;
  }

  /**
   * Submit weather measurement to the weather system.
   *
   * @param measure the weather measure to store.
   * @return the response of the invocation.
   */
  @POST
  @Path("/measurement")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response submit(@NotNull(message = "Measurement cannot be null!") Weather measure) {
    Measure entity = new Measure(measure.getTemperature(), measure.getWeatherCode(),
        LocalDateTime.parse(measure.getMeasureTime()).atZone(ZoneOffset.UTC));
    if (!Strings.isNullOrEmpty(measure.getCity()) && !Strings.isNullOrEmpty(measure.getCountry())) {
      measurementService.storeMeasure(measure.getCity(), measure.getCountry(), entity);
      return Response.ok().build();
    } else if (measure.getLocation() != null) {
      Weather.Location location = measure.getLocation();
      measurementService.storeMeasure(location.getLongitude(), location.getLatitude(), entity);
      return Response.ok().build();
    }
    return Response.status(SC_BAD_REQUEST).build();
  }
}
