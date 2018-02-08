package ua.danit.rest.weatherapp.resources;

import ua.danit.rest.weatherapp.ext.MeasurementService;

/**
 * Service for storing information about weather.
 *
 * @author Andrey Minov
 */
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
}
