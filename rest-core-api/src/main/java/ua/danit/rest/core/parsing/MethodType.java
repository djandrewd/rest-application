package ua.danit.rest.core.parsing;

/**
 * Enumeration for supported HTTP methods.
 *
 * @author Andrey Minov
 */
public enum MethodType {
  GET(false), POST(true);

  private boolean supportBody;

  MethodType(boolean supportBody) {
    this.supportBody = supportBody;
  }

  public boolean isSupportBody() {
    return supportBody;
  }
}
