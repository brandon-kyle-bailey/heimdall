package heimdall.common.interfaces;

import org.json.JSONObject;

public interface IEventObserverDriver {
  public boolean hasPermissions();

  public void requestPermissions();

  public boolean isIdle();

  public boolean isSuspended();

  public boolean isLocked();

  public boolean isLidClosed();

  public boolean stateChangeIsSame(JSONObject last, JSONObject current);

  public String getAppName(String payload);

  public String getAppTitle(String payload);

  public String getAppUrl(String appType, String payload);

  public JSONObject getCurrentSystemState();

  public void run();
}
