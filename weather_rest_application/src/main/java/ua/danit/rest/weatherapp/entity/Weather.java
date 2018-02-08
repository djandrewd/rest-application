package ua.danit.rest.weatherapp.entity;

import javax.annotation.Nonnull;

/**
 * Holds information about weather inside some region, city or location.
 *
 * @author Andrey Minov
 */
public class Weather {
  private String city;
  private String country;
  private Location location;
  @Nonnull
  private WeatherCode weatherCode;
  @Nonnull
  private Double temperature;
  @Nonnull
  private String measureTime;


  /**
   * Instantiates a new Weather instance.
   *
   * @param city             the city where to check the weather
   * @param country          the country code of the city
   * @param location         the location with latitude and longitude
   * @param weatherCode      the weather code in the location
   * @param temperature      the temperature in the location
   * @param measureTimeLocal time of measure in UTC in ISO-8601 format.
   *                         Check {@link java.time.format.DateTimeFormatter}
   */
  public Weather(String city, String country, Location location, WeatherCode weatherCode,
                 Double temperature, String measureTimeLocal) {
    this.city = city;
    this.country = country;
    this.location = location;
    this.weatherCode = weatherCode;
    this.temperature = temperature;
    this.measureTime = measureTimeLocal;
  }

  public String getCity() {
    return city;
  }

  public String getCountry() {
    return country;
  }

  public Location getLocation() {
    return location;
  }

  public WeatherCode getWeatherCode() {
    return weatherCode;
  }

  public Double getTemperature() {
    return temperature;
  }

  public String getMeasureTime() {
    return measureTime;
  }

  /**
   * Holds information about Geo2 location by latitude and longitude.
   *
   * @author Andrey Minov
   */
  public static class Location {
    private double longitude;
    private double latitude;

    /**
     * Instantiates a new Location.
     *
     * @param longitude the longitude
     * @param latitude  the latitude
     */
    public Location(double longitude, double latitude) {
      this.longitude = longitude;
      this.latitude = latitude;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      Location location = (Location) o;

      if (Double.compare(location.longitude, longitude) != 0) {
        return false;
      }
      return Double.compare(location.latitude, latitude) == 0;
    }

    @Override
    public int hashCode() {
      int result;
      long temp;
      temp = Double.doubleToLongBits(longitude);
      result = (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(latitude);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      return result;
    }

    public double getLongitude() {
      return longitude;
    }

    public double getLatitude() {
      return latitude;
    }
  }
}
