package heimdall.handlers;

import org.json.JSONObject;

import heimdall.ports.LoggingPort;
import heimdall.services.UserSessionService;

public class CreateUserEventHandler {
  private LoggingPort logManager;
  private UserSessionService service;

  public CreateUserEventHandler(LoggingPort logManager, UserSessionService service) {
    this.logManager = logManager;
    this.service = service;
  }

  public void handle(JSONObject event) {
    this.logManager.debug("CreateUserEventHandler.handle invoked: %s".formatted(event.toString()));
    this.service.create(event.getString("userId"), event.getString("accountId"));
  }
}
