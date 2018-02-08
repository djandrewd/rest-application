# REST framework and application

You task will be implement simple REST framework and write simple application which uses it.

## REST framework

In this part you have to implement basic interfaces for _JSR 339: JAX-RS 2.0: The Java API for 
RESTful Web Services._


### Data provided

You will be provided with maven module : _rest-core-api_ which contains all needed dependencies for 
application. You can extend that list if you need.

#### Entities

Below is list of provided entities and classes.

1. Invocation - class holds information about REST method together with types, convertors and 
parameters.
2. ConvertersStore - class holds information about convertors from-to parameters of request and 
response object using java class or content type. See annotations ```@Consumes, @Produces```
3. Reflections - utility class for transforming Invocation object into service method incoming 
parameters.
4. ServicesStore - class responsible for holding information about services metadata together 
with cached service objects.
5. ApplicationBuilder - builder for RestApplication object. 

### Tasks 

You have to implement next classes

1. ReflectionServiceParser - class responsible for parsing Class<?> file of the service and 
transforming it into list of Invocations using reflections.
2. RestApplication - class presented REST application which can be started and stopped. (use 
provided Jetty dependency).
3. DispatcherServlet - main servlet entry point to application. All other services will be 
called using reflection from this servlet.

Implementation of _RestApplication.start_ method should start Jetty or other embedded HTTP server, 
register dispatcher servlet, parse services using ReflectionServiceParser and register them into
ServicesStore. You also have to implement supplier for service classes from Class<?> objects. Use
simple reflection assuming service class has default constructor. 

### Checks 

Implementation should satisfy ```ParsingMethodsTest```  for ```ReflectionServiceParser``` and 
follow code conventions and Google checkstyle rules.

## Weather measurement service

Using framework written in first part of the task you should write REST application provided 
information about weather measurements at some city or geo location.

### API

Your implementation should implement resources below.
All resources should return HTTP error:
1. 400 - in case some of required parameter is missing.
2. 404 - when resource is not found.
3. 500 - when internal error happens.

#### Get measurement by city (/resources/weather/get/byCity)
HTTP method:  _GET_

Incoming parameters

1. _city_ (REQUIRED, String) - name of the city of the measurement.
2. _country_ (REQUIRED, String) - name of the country of the measurement.

Response example: 
```json
{
  "city": "Kiev",
  "country": "Ukraine",
  "weatherCode": "CLOUDY",
  "temperature": 7,
  "measureTime": "2017-11-14T10:00:00"
}
```

#### Get weather by location (/resources/weather/get/byLocation) 
HTTP method:  _GET_

Incoming parameters

1. _longitude_ (REQUIRED, double) - geo longitude of the measure place.
2. _latitude_ (REQUIRED, double) - geo latitude of the measure place.

Response example

```json
{
   "location":    {
      "longitude": 51,
      "latitude": 31
   },
   "weatherCode": "CLOUDY",
   "temperature": 7,
   "measureTime": "2017-11-14T10:00:00"
}
```
#### Submit weather measurement (/resources/weather/submit/measurement)
HTTP method: POST

Incoming parameters: NONE

Body: 
   - _Content-Type_ - application/json
   - _Value_ - serialized json of _ua.danit.rest.weatherapp.entity.Weather_ class.
   
Example request:
```
POST http://localhost:8080/resources/weather/submit/measurement HTTP/1.1
Accept-Encoding: gzip,deflate
Content-Type: application/json
Content-Length: 128

{"location": {"longitude": 51.0, "latitude": 31.0},"weatherCode":"CLOUDY","temperature":7.0,"measureTime":"2017-11-14T10:00:00"}
```   
 
### Data

You will be privided with next classes

1. WeatherSelectResource - holds service methods responsible for selection of weather measurement.
2. WeatherStoreResource - hold servide methods responsible for submitting weather measurements.
3. MeasurementService - storage service for storing weather measurement.
 
### Checks 

Implementation should satisfy ```WeatherApiTest``` and follow code conventions and Google checkstyle rules.

Good luck!