package heimdall.common.interfaces;

import org.json.JSONObject;

public interface IEventHandler {
  public Object handle(JSONObject event);
}
