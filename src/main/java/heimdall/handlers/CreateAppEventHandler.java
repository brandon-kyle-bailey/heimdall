
package heimdall.handlers;

import org.json.JSONObject;

import heimdall.common.interfaces.IEventHandler;
import heimdall.ports.LoggerPort;

public class CreateAppEventHandler implements IEventHandler {

  @Override
  public Object handle(JSONObject event) {
    LoggerPort.debug("CreateAppEventHandler.handle invoked: %s".formatted(event));
    return event;
  }

}
