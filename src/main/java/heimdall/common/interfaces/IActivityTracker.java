package heimdall.common.interfaces;

public interface IActivityTracker {

  public boolean hasPermissions();

  public void requestPermissions();

  public boolean isIdle();

  public boolean isSuspended();

  public boolean isLidClosed();

  public Object getActiveWindow();

  public void run();

}
