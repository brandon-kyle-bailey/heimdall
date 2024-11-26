package heimdall.interfaces.controllers.app;

import org.json.JSONObject;

import heimdall.common.abstracts.AEventHandler;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.commands.app.CreateAppQuery;

public class CreateAppEventController extends AEventHandler {

  public CreateAppEventController(LoggingPort logManager, CreateAppQuery query) throws NoSuchMethodException {
    super(logManager, query);
  }

  @Override
  public void handle(JSONObject event) {
    this.logManager.debug("CreateAppEventController.handle invoked: %s".formatted(event));
    this.query.handle(event);
  }
}
