package ua.danit.rest.weatherapp.impl.resources;

import static java.lang.Math.floor;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotEmpty;
import ua.danit.rest.weatherapp.entity.Measure;
import ua.danit.rest.weatherapp.entity.Weather;
import ua.danit.rest.weatherapp.ext.MeasurementService;

/**
 * The type Weather service.
 */
@Path("/weather/get")
public class WeatherSelectResource {

  private MeasurementService measurementService;

  /**
   * Instantiates a new Weather service.
   *
   * @param measurementService the measurement service
   */
  public WeatherSelectResource(MeasurementService measurementService) {
    this.measurementService = measurementService;
  }

  /**
   * Gets last measured weather by city.
   *
   * @param city    the city where to check the weather.
   * @param country the country where to check the weather.
   * @return last measured weather by city.
   */
  @GET
  @Path("/byCity")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getWeather(
      @NotEmpty(message = "City cannot be empty!") @QueryParam("city") String city,
      @NotEmpty(message = "Country cannot be empty!") @QueryParam("country") String country) {
    Measure measure = measurementService.getCurrentWeather(city, country);
    if (measure == null) {
      return Response.ok().build();
    }
    return Response.ok().entity(
        new Weather(city, country, null, measure.getCode(), measure.getTemperature(),
            measure.getMeasureTimeUtc().withZoneSameInstant(ZoneOffset.UTC)
                   .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))).build();
  }

  /**
   * Gets last measured weather by geo location.
   *
   * @param longitude the longtitude of the measured location.
   * @param latitude  the latitude of the measured location.
   * @return last measured weather by geo location.
   */
  @GET
  @Path("/byLocation")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getWeather(
      @NotNull(message = "Longitute cannot be empty!") @QueryParam("longitude") double longitude,
      @NotNull(message = "Latitude cannot be empty!") @QueryParam("latitude") double latitude) {
    Measure measure = measurementService.getCurrentWeather(floor(longitude), floor(latitude));
    if (measure == null) {
      return Response.ok().build();
    }
    return Response.ok().entity(
        new Weather(null, null, new Weather.Location(longitude, latitude), measure.getCode(),
            measure.getTemperature(),
            measure.getMeasureTimeUtc().withZoneSameInstant(ZoneOffset.UTC)
                   .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))).build();
  }
}
