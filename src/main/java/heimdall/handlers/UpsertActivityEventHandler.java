
package heimdall.handlers;

import org.json.JSONObject;

import heimdall.common.interfaces.IEventHandler;
import heimdall.ports.LoggerPort;

public class UpsertActivityEventHandler implements IEventHandler {

  @Override
  public Object handle(JSONObject event) {
    LoggerPort.debug("UpsertActivityEventHandler.handle invoked: %s".formatted(event));
    return event;
  }

}
