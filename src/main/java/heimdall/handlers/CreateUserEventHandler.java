package heimdall.handlers;

import org.json.JSONObject;

import heimdall.common.interfaces.IEventHandler;
import heimdall.ports.LoggerPort;

public class CreateUserEventHandler implements IEventHandler {

  @Override
  public Object handle(JSONObject event) {
    LoggerPort.debug("CreateUserEventHandler.handle invoked: %s".formatted(event));
    return event;
  }

}
