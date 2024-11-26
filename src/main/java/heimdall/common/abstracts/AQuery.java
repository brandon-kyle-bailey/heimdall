package heimdall.common.abstracts;

import org.json.JSONObject;

import heimdall.infrastructure.ports.logging.LoggingPort;

public abstract class AQuery {
  protected LoggingPort logManager;

  public AQuery(LoggingPort logManager) throws NoSuchMethodException {
    super();
  }

  public abstract void handle(JSONObject event);
}
