package heimdall.common.interfaces;

import org.json.JSONObject;

public interface IActivityTracker {

  public boolean hasPermissions();

  public void requestPermissions();

  public boolean isIdle();

  public boolean isSuspended();

  public boolean isLidClosed();

  public boolean currentActivityEqualsLastActivity(JSONObject currentActivity, JSONObject lastActivity);

  public JSONObject getActiveWindow();

  public void run();

}
