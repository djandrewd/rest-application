package ua.danit.rest.weatherapp.entity;

import com.google.common.base.MoreObjects;

import java.time.ZonedDateTime;

/**
 * Describes measure at the location X.
 *
 * @author Andrey Minov
 */
public class Measure {
  private double temperature;
  private WeatherCode code;
  private ZonedDateTime measureTimeUtc;

  /**
   * Instantiates a new Measure instance.
   *
   * @param temperature    the temperature at location during measure
   * @param code           the code of the weather at location
   * @param measureTimeUtc the measure time utc.
   */
  public Measure(double temperature, WeatherCode code, ZonedDateTime measureTimeUtc) {
    this.temperature = temperature;
    this.code = code;
    this.measureTimeUtc = measureTimeUtc;
  }

  public double getTemperature() {
    return temperature;
  }

  public WeatherCode getCode() {
    return code;
  }

  public ZonedDateTime getMeasureTimeUtc() {
    return measureTimeUtc;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Measure measure = (Measure) o;

    if (Double.compare(measure.temperature, temperature) != 0) {
      return false;
    }
    if (code != measure.code) {
      return false;
    }
    return measureTimeUtc != null ? measureTimeUtc.equals(measure.measureTimeUtc) :
        measure.measureTimeUtc == null;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(temperature);
    result = (int) (temp ^ (temp >>> 32));
    result = 31 * result + (code != null ? code.hashCode() : 0);
    result = 31 * result + (measureTimeUtc != null ? measureTimeUtc.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("temperature", temperature).add("code", code)
                      .add("measureTimeUtc", measureTimeUtc).toString();
  }
}
