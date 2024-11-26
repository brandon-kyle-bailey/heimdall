
package heimdall.common.abstracts;

import org.json.JSONObject;

import heimdall.infrastructure.ports.logging.LoggingPort;

public abstract class AEventHandler {
  protected LoggingPort logManager;

  protected AQuery query;

  public AEventHandler(LoggingPort logManager, AQuery query) throws NoSuchMethodException {
    super();
    this.getClass().getConstructor(LoggingPort.class, AQuery.class);
  }

  public abstract void handle(JSONObject event);
}
