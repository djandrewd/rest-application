package ua.danit.rest.weatherapp.resources;

import ua.danit.rest.weatherapp.ext.MeasurementService;

/**
 * Service for selection of current weather measurements.
 *
 * @author Andrey Minov
 */
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
}
