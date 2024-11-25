package heimdall.adapters;

import java.time.LocalDateTime;

import org.json.JSONObject;

import heimdall.common.enums.EDomainEvents;
import heimdall.common.interfaces.IActivityTracker;
import heimdall.ports.LoggerPort;

public class LinuxActivityAdapter implements IActivityTracker {

  private EventbusAdapter _eventbus;
  private WebsocketAdapter _websocket;

  public LinuxActivityAdapter(EventbusAdapter eventbus, WebsocketAdapter websocket) {
    this._eventbus = eventbus;
    this._websocket = websocket;
  }

  @Override
  public boolean hasPermissions() {
    return true;
  }

  @Override
  public void requestPermissions() {
  }

  @Override
  public boolean isIdle() {
    return false;
  }

  @Override
  public boolean isSuspended() {
    return false;
  }

  @Override
  public boolean isLidClosed() {
    return false;
  }

  @Override
  public JSONObject getActiveWindow() {
    return new JSONObject();
  }

  @Override
  public boolean currentActivityEqualsLastActivity(JSONObject currentActivity, JSONObject lastActivity) {
    if (currentActivity == null || lastActivity == null) {
      return currentActivity == lastActivity;
    }

    boolean namesMatch = currentActivity.optString("name").equals(lastActivity.optString("name"));
    boolean titlesMatch = currentActivity.optString("title").equals(lastActivity.optString("title"));
    boolean urlsMatch = currentActivity.optString("url").equals(lastActivity.optString("url"));

    return namesMatch && titlesMatch && urlsMatch;
  }

  @Override
  public void run() {
    if (!hasPermissions()) {
      requestPermissions();
      return;
    }
    JSONObject lastActivity = null;
    while (true) {
      JSONObject currentActivity = this.getActiveWindow();
      if (currentActivity != null && !currentActivityEqualsLastActivity(currentActivity, lastActivity)) {
        LoggerPort.debug("Activity changed: %s -> %s".formatted(lastActivity, currentActivity));

        this._eventbus.publish(EDomainEvents.CREATE_APP.toString(), currentActivity);

        if (lastActivity != null) {
          lastActivity.put("endTime", LocalDateTime.now().toString());
          this._eventbus.publish(EDomainEvents.UPSERT_ACTIVITY.toString(), lastActivity);
        }

        currentActivity.put("startTime", LocalDateTime.now().toString());
        this._eventbus.publish(EDomainEvents.UPSERT_ACTIVITY.toString(), currentActivity);

        lastActivity = currentActivity;

        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          LoggerPort.error("Sleep interrupted: %s".formatted(e.getMessage()));
        }
      }
    }
  }

  @Override
  public String getApp(String payload) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getApp'");
  }

  @Override
  public String getTitle(String payload) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getTitle'");
  }

  @Override
  public String getUrl(String payload) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getUrl'");
  }
}
