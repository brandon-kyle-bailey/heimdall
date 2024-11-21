package heimdall.ports;

import heimdall.common.interfaces.IActivityTracker;

public class ActivityTrackerPort implements Runnable {
  private IActivityTracker _adapter;

  public ActivityTrackerPort(IActivityTracker adapter) {
    this._adapter = adapter;
  }

  @Override
  public void run() {
    this._adapter.run();
  }

}
