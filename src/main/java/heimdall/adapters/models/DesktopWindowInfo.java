package heimdall.adapters.models;

import java.time.LocalDateTime;
import java.util.Objects;

import common.shared.inter.IWindowInfo;

public class DesktopWindowInfo implements IWindowInfo {
  private String applicationName;
  private String windowTitle;
  private LocalDateTime timestamp;

  public DesktopWindowInfo(String applicationName, String windowTitle, LocalDateTime timestamp) {
    this.applicationName = applicationName;
    this.windowTitle = windowTitle;
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DesktopWindowInfo that = (DesktopWindowInfo) o;
    return Objects.equals(applicationName, that.applicationName) &&
        Objects.equals(windowTitle, that.windowTitle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(applicationName, windowTitle);
  }

  @Override
  public String getApplicationName() {
    return applicationName;
  }

  @Override
  public String getWindowTitle() {
    return windowTitle;
  }

  @Override
  public String getUrl() {
    return windowTitle;
  }

  @Override
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return "Application: " + applicationName + ", Window Title: " + windowTitle + ", Timestamp: " + timestamp;
  }
}
