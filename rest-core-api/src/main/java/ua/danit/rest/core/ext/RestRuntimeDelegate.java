package ua.danit.rest.core.ext;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * Runtime delegate for RS environments.
 * Uses to create {@link javax.ws.rs.core.Response.ResponseBuilder}
 * and for further creation of {@link javax.ws.rs.core.Response}. Other functionality is disabled
 * and will throw {@link UnsupportedOperationException}.
 *
 * @author Andrey Minov.
 */
public class RestRuntimeDelegate extends RuntimeDelegate {

  @Override
  public UriBuilder createUriBuilder() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response.ResponseBuilder createResponseBuilder() {
    return new RestResponseBuilder();
  }

  @Override
  public Variant.VariantListBuilder createVariantListBuilder() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T createEndpoint(Application application, Class<T> endpointType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Link.Builder createLinkBuilder() {
    throw new UnsupportedOperationException();
  }


}
