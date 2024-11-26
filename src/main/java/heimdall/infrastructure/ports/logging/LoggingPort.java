package heimdall.infrastructure.ports.logging;

import heimdall.infrastructure.adapters.logging.LoggingAdapter;

public class LoggingPort {

  private final String PREFIX = "[Heimdall::LoggerPort::";
  private final String INFO = "info";
  private final String DEBUG = "debug";
  private final String WARN = "warn";
  private final String ERROR = "error";

  protected LoggingAdapter adatper;

  public LoggingPort(LoggingAdapter adapter) {
    this.adatper = adapter;
  }

  public void info(String message) {
    this.adatper.log("%s%s]: %s".formatted(this.PREFIX, this.INFO, message));
  }

  public void debug(String message) {
    this.adatper.log("%s%s]: %s".formatted(this.PREFIX, this.DEBUG, message));
  }

  public void warn(String message) {
    this.adatper.log("%s%s]: %s".formatted(this.PREFIX, this.WARN, message));
  }

  public void error(String message) {
    this.adatper.log("%s%s]: %s".formatted(this.PREFIX, this.ERROR, message));
  }
}
