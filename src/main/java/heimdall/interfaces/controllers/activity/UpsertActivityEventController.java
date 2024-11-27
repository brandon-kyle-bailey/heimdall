package heimdall.interfaces.controllers.activity;

import org.json.JSONObject;

import heimdall.common.abstracts.AEventHandler;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.commands.activity.UpsertActivityQuery;
import heimdall.interfaces.dtos.activity.UpsertActivityDto;

public class UpsertActivityEventController implements AEventHandler<UpsertActivityDto> {
  private LoggingPort logManager;
  private UpsertActivityQuery query;

  public UpsertActivityEventController(LoggingPort logManager, UpsertActivityQuery query) {
    this.logManager = logManager;
    this.query = query;
  }

  @Override
  public void handle(UpsertActivityDto event) {
    this.logManager.debug("UpsertActivityEventController.handle invoked: %s".formatted(event));
    this.query.handle(event);
  }
}
