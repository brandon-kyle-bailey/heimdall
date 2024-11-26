package heimdall.common.abstracts;

import org.json.JSONObject;

import heimdall.infrastructure.adapters.persistence.PersistenceAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;

public abstract class AApplicationService<T> {
  protected LoggingPort logManager;

  protected PersistenceAdapter<T> port;

  public AApplicationService(LoggingPort logManager, PersistenceAdapter<T> port) throws NoSuchMethodException {
    super();
    this.getClass().getConstructor(LoggingPort.class, AQuery.class);
  }

  public abstract void handle(JSONObject event);
}
