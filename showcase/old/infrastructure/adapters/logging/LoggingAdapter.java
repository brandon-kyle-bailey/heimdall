package heimdall.infrastructure.adapters.logging;

// Defined this way so that we can support log shipping in the future
public class LoggingAdapter {
  protected final boolean debug;

  public LoggingAdapter(boolean debug) {
    this.debug = debug;
  }

  public void log(String message) {
    if (this.debug) {
      System.out.println(message);
    }
  }
}
