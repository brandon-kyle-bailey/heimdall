package heimdall.adapters;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggingAdapter {
  private static final String LOG_FILE = FileSystemAdapter.LocalStoragePath() + "p8.log"; // Log file path
  private static PrintWriter logWriter;

  static {
    try {
      // Create a PrintWriter to write to the log file
      logWriter = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)));
    } catch (IOException e) {
      System.err.println("Error initializing log file writer: " + e.getMessage());
    }
  }

  // Format for timestamps in logs
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  // Logs the message to both the terminal and the log file
  public static void log(String message, boolean debug) {
    String timestamp = dateFormat.format(new Date());
    String formattedMessage = String.format("[%s] %s", timestamp, message);

    if (debug) {
      // Log to terminal
      System.out.println(formattedMessage);
    }

    // Log to file
    if (logWriter != null) {
      logWriter.println(formattedMessage);
      logWriter.flush(); // Ensure the message is written to the file
    }
  }

  // Close the log writer when the application exits
  public static void close() {
    if (logWriter != null) {
      logWriter.close();
    }
  }
}
