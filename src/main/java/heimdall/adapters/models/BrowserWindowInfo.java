package heimdall.adapters.models;

import java.util.Objects;

import common.shared.inter.IWindowInfo;

public class BrowserWindowInfo implements IWindowInfo {
  private String browserName;
  private String tabTitle;
  private String tabURL;

  public BrowserWindowInfo(String browserName, String tabTitle, String tabURL) {
    this.browserName = browserName;
    this.tabTitle = tabTitle;
    this.tabURL = tabURL;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    BrowserWindowInfo that = (BrowserWindowInfo) o;
    return Objects.equals(browserName, that.browserName) &&
        Objects.equals(tabTitle, that.tabTitle) &&
        Objects.equals(tabURL, that.tabURL);
  }

  @Override
  public int hashCode() {
    return Objects.hash(browserName, tabTitle, tabURL);
  }

  @Override
  public String getApplicationName() {
    return browserName;
  }

  @Override
  public String getWindowTitle() {
    return tabTitle;
  }

  @Override
  public String getAdditionalInfo() {
    return "URL: " + tabURL;
  }

  @Override
  public String toString() {
    return "Browser: " + browserName + ", Title: " + tabTitle + ", URL: " + tabURL;
  }
}
