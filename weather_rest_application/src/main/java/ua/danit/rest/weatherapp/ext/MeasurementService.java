package ua.danit.rest.weatherapp.ext;

import ua.danit.rest.weatherapp.entity.Measure;

/**
 * Service which calls third party weather service and return measurement.
 *
 * @author Andrey Minov
 */
public interface MeasurementService {
  /**
   * Gets last weather measurement in provided city and country.
   *
   * @param city    the city to check weather.
   * @param country the country of the provided city.
   * @return the current weather measurement in provided city.
   */
  Measure getCurrentWeather(String city, String country);

  /**
   * Gets last weather at location of longitude and latitude.
   *
   * @param longitude the longitude of the location
   * @param latitude  the latitude of the location
   * @return the current weather at geographical location provided.
   */
  Measure getCurrentWeather(double longitude, double latitude);

  /**
   * Store measurement of the weather for location city.
   *
   * @param city    the city to store the weather
   * @param country the country to store the weather
   * @param measure the measure to store.
   */
  void storeMeasure(String city, String country, Measure measure);

  /**
   * Store measure of the weather for geo location.
   *
   * @param longitude the longitude of the place
   * @param latitude  the latitude of the place
   * @param measure   the measurement of the weather.
   */
  void storeMeasure(double longitude, double latitude, Measure measure);


}
