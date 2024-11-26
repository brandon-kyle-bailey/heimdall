package heimdall.interfaces.commands.activity;

import org.json.JSONObject;

import heimdall.common.abstracts.AQuery;
import heimdall.infrastructure.ports.logging.LoggingPort;

public class UpsertActivityQuery extends AQuery {

  public UpsertActivityQuery(LoggingPort logManager) throws NoSuchMethodException {
    super(logManager);
  }

  @Override
  public void handle(JSONObject event) {
    this.logManager.debug("UpsertActivityQuery.handle invoked: %s".formatted(event));

  }
}
