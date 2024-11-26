package heimdall.interfaces.controllers.activity;

import org.json.JSONObject;

import heimdall.common.abstracts.AEventHandler;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.commands.activity.UpsertActivityQuery;

public class UpsertActivityEventController extends AEventHandler {

  public UpsertActivityEventController(LoggingPort logManager, UpsertActivityQuery query) throws NoSuchMethodException {
    super(logManager, query);
  }

  @Override
  public void handle(JSONObject event) {
    this.logManager.debug("UpsertActivityEventController.handle invoked: %s".formatted(event));
    this.query.handle(event);
  }
}
