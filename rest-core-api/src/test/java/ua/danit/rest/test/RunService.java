package ua.danit.rest.test;

import java.util.Arrays;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Test run service class
 *
 * @author Andrey Minov
 */
@Path("/run")
class RunService {
  @GET
  @Path("/workouts")
  public int getWorkouts(@QueryParam("day") Integer day, @QueryParam("month") Integer month,
                         @HeaderParam("year") Long year) {
    return 1;
  }

  @POST
  @Path("/pace")
  public int submitPace(@QueryParam("pace") Integer pace, String time) {
    return 1;
  }

  @POST
  @Path("/map")
  @Consumes(MediaType.APPLICATION_JSON)
  public int submitRunMap(MapEntry map) {
    return 1;
  }

  @POST
  @Path("/binary")
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  public int submitRunMap(byte[] array) {
    return 1;
  }

  public static class MapEntry {
    private String city;
    private String[] route;

    MapEntry(String city, String[] route) {
      this.city = city;
      this.route = route;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      MapEntry mapEntry = (MapEntry) o;

      if (city != null ? !city.equals(mapEntry.city) : mapEntry.city != null) {
        return false;
      }
      return Arrays.equals(route, mapEntry.route);
    }

    @Override
    public int hashCode() {
      int result = city != null ? city.hashCode() : 0;
      result = 31 * result + Arrays.hashCode(route);
      return result;
    }
  }
}
