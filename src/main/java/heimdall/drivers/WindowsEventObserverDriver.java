package heimdall.drivers;

import org.json.JSONObject;

import heimdall.common.interfaces.IEventObserverDriver;
import heimdall.ports.LoggingPort;
import heimdall.services.ActivityService;
import heimdall.services.AppWatcherService;

public class WindowsEventObserverDriver implements IEventObserverDriver {

  private LoggingPort logManager;
  private AppWatcherService appService;
  private ActivityService activityService;

  public WindowsEventObserverDriver(LoggingPort logManager, AppWatcherService appService,
      ActivityService activityService) {
    this.logManager = logManager;
    this.appService = appService;
    this.activityService = activityService;
  }

  @Override
  public boolean hasPermissions() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'hasPermissions'");
  }

  @Override
  public void requestPermissions() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'requestPermissions'");
  }

  @Override
  public boolean isIdle() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isIdle'");
  }

  @Override
  public boolean isSuspended() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isSuspended'");
  }

  @Override
  public boolean isLocked() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isLocked'");
  }

  @Override
  public boolean stateChangeIsSame(JSONObject last, JSONObject current) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'stateChangeIsSame'");
  }

  @Override
  public String getAppName(String payload) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAppName'");
  }

  @Override
  public String getAppTitle(String appName, String payload) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAppTitle'");
  }

  @Override
  public String getAppUrl(String appName, String payload) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAppUrl'");
  }

  @Override
  public JSONObject getCurrentSystemState() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getCurrentSystemState'");
  }

  @Override
  public void run() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'run'");
  }
}
