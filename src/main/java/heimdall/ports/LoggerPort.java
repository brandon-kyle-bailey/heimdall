package heimdall.ports;

public class LoggerPort {

  public static void info(String message) {
    System.out.println("[Heimdall::LoggerPort::info]: %s".formatted(message));
  }

  public static void debug(String message) {
    System.out.println("[Heimdall::LoggerPort::debug]: %s".formatted(message));
  }

  public static void warn(String message) {
    System.out.println("[Heimdall::LoggerPort::warn]: %s".formatted(message));
  }

  public static void error(String message) {
    System.err.println("[Heimdall::LoggerPort::err]: %s".formatted(message));
  }
}
