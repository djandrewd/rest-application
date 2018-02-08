package ua.danit.rest.weatherapp.impl;

import static ua.danit.rest.core.ApplicationBuilder.builder;

import ua.danit.rest.core.ReflectionServiceSupplier;
import ua.danit.rest.core.RestApplication;
import ua.danit.rest.core.RestApplicationDelegate;
import ua.danit.rest.core.parsing.RealReflectionServiceParser;
import ua.danit.rest.weatherapp.impl.ext.InMemoryMeasureService;
import ua.danit.rest.weatherapp.impl.resources.WeatherSelectResource;
import ua.danit.rest.weatherapp.impl.resources.WeatherStoreResource;

/**
 * Main weather application entry point.
 *
 * @author Andrey Minov
 */
public class WeatherApplication {
  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {
    InMemoryMeasureService measureService = new InMemoryMeasureService();
    WeatherSelectResource weatherService = new WeatherSelectResource(measureService);
    WeatherStoreResource weatherStoreResource = new WeatherStoreResource(measureService);
    RestApplication delegate = builder().withPort(8080).withMatchingUrls("/resources/*")
                                        .withServiceInstance(weatherService)
                                        .withServiceInstance(weatherStoreResource)
                                        .build(new ReflectionServiceSupplier(),
                                            new RealReflectionServiceParser()::parse);
    RestApplicationDelegate application = new RestApplicationDelegate(delegate);
    application.start();
  }
}