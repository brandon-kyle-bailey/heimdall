package heimdall.infrastructure.adapters.drivers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileDriver {

  public static void mkdirs(String dirs) {
    try {
      Files.createDirectories(Paths.get(dirs));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
