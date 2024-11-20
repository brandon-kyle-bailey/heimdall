package heimdall.adapters.models;

import java.util.Objects;

import common.shared.inter.IWindowInfo;

public class DesktopWindowInfo implements IWindowInfo {
  private String applicationName;
  private String windowTitle;

  public DesktopWindowInfo(String applicationName, String windowTitle) {
    this.applicationName = applicationName;
    this.windowTitle = windowTitle;
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
  public String getAdditionalInfo() {
    return "No additional info for desktop application.";
  }

  @Override
  public String toString() {
    return "Application: " + applicationName + ", Window Title: " + windowTitle;
  }
}
