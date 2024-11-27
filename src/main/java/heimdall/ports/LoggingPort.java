package heimdall.ports;

import heimdall.adapters.LoggingAdapter;

public class LoggingPort {

  private final String PREFIX = "[Heimdall::LoggerPort::";
  private final String INFO = "info";
  private final String DEBUG = "debug";
  private final String WARN = "warn";
  private final String ERROR = "error";

  protected boolean isDebug;

  public LoggingPort(boolean isDebug) {
    this.isDebug = isDebug;
  }

  public void info(String message) {
    LoggingAdapter.log("%s%s]: %s".formatted(this.PREFIX, this.INFO, message), this.isDebug);
  }

  public void debug(String message) {
    LoggingAdapter.log("%s%s]: %s".formatted(this.PREFIX, this.DEBUG, message), this.isDebug);
  }

  public void warn(String message) {
    LoggingAdapter.log("%s%s]: %s".formatted(this.PREFIX, this.WARN, message), this.isDebug);
  }

  public void error(String message) {
    LoggingAdapter.log("%s%s]: %s".formatted(this.PREFIX, this.ERROR, message), this.isDebug);
  }
}
