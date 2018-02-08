package ua.danit.rest.weatherapp.impl.ext;

import static java.lang.Math.floor;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Function;

import ua.danit.rest.weatherapp.entity.Measure;
import ua.danit.rest.weatherapp.entity.Weather.Location;
import ua.danit.rest.weatherapp.ext.MeasurementService;

/**
 * Service for holding information about weather measurements in memory.
 * <p/>
 * Location is truncated to integral number (in floor model).
 *
 * @author Andrey Minov
 */
public class InMemoryMeasureService implements MeasurementService {

  private static final String CITY_COUNTRY_PATTERN = "%1$s/%2$s";
  private static final int INITIAL_CAPACITY = 100;

  private Map<String, Queue<Measure>> cityMeasurement;
  private Map<Location, Queue<Measure>> locationMeasurement;

  /**
   * Instantiates a new In memory measure service.
   */
  public InMemoryMeasureService() {
    this.cityMeasurement = new ConcurrentHashMap<>();
    this.locationMeasurement = new ConcurrentHashMap<>();
  }

  @Override
  public Measure getCurrentWeather(String city, String country) {
    String key = String.format(CITY_COUNTRY_PATTERN, city, country);
    return ofNullable(cityMeasurement.get(key)).map(Queue::peek).orElse(null);
  }

  @Override
  public Measure getCurrentWeather(double longitude, double latitude) {
    Location key = new Location(floor(longitude), floor(latitude));
    return ofNullable(locationMeasurement.get(key)).map(Queue::peek).orElse(null);
  }

  @Override
  public void storeMeasure(String city, String country, Measure measure) {
    String key = String.format(CITY_COUNTRY_PATTERN, city, country);
    cityMeasurement.computeIfAbsent(key, createEntrySupplier()).offer(measure);
  }

  @Override
  public void storeMeasure(double longitude, double latitude, Measure measure) {
    Location key = new Location(floor(longitude), floor(latitude));
    locationMeasurement.computeIfAbsent(key, createEntrySupplier()).offer(measure);
  }

  private <T> Function<? super T, Queue<Measure>> createEntrySupplier() {
    return k -> new PriorityBlockingQueue<>(INITIAL_CAPACITY,
        comparing(Measure::getMeasureTimeUtc));
  }
}
