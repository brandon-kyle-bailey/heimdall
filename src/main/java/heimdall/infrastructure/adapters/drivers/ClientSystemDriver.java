package heimdall.infrastructure.adapters.drivers;

public class ClientSystemDriver {

  public static String getOperatingSystem() {
    return System.getProperty("os.name").toLowerCase();
  }

  public static String LocalStoragePath() {
    String os = System.getProperty("os.name").toLowerCase();
    String separator = System.getProperty("file.separator").toLowerCase();
    String appName = "heimdall";
    String dbPath;

    if (os.contains("win")) {
      dbPath = System.getenv("APPDATA") + separator + "Local" + separator + appName + separator;
    } else if (os.contains("mac")) {
      dbPath = System.getProperty("user.home") + separator + "Library" + separator + "Application Support" + separator
          + appName + separator;
    } else if (os.contains("nix") || os.contains("nux")) {
      dbPath = System.getProperty("user.home") + separator + ".local" + separator + "share" + separator + appName
          + separator;
    } else {
      dbPath = System.getProperty("user.home") + separator + "." + appName + separator;
    }
    FileDriver.mkdirs(dbPath);
    return dbPath;
  }
}
