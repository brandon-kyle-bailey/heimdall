
package heimdall.drivers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

import org.json.JSONObject;

import heimdall.adapters.WebsocketAdapter;
import heimdall.common.interfaces.IEventObserverDriver;
import heimdall.ports.LoggingPort;
import heimdall.services.ActivityService;
import heimdall.services.AppWatcherService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MacosEventObserverDriver implements IEventObserverDriver {

  private LoggingPort logManager;
  private AppWatcherService appService;
  private ActivityService activityService;
  private WebsocketAdapter websocket;

  public MacosEventObserverDriver(LoggingPort logManager, AppWatcherService appService,
      ActivityService activityService, WebsocketAdapter websocket) {
    this.logManager = logManager;
    this.appService = appService;
    this.activityService = activityService;
    this.websocket = websocket;
  }

  private String extractValue(String part) {
    String[] splitPart = part.split(": ");
    if (splitPart.length > 1) {
      return splitPart[1].trim();
    }
    return "Unknown";
  }

  @Override
  public boolean hasPermissions() {
    try {
      String script = "tell application \"System Events\" to return (exists (process 1))";
      ProcessBuilder processBuilder = new ProcessBuilder("osascript", "-e", script);
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        if ("true".equals(line.trim())) {
          return true;
        }
      }

    } catch (Exception e) {
      e.getStackTrace();
    }
    return false;
  }

  @Override
  public void requestPermissions() {
    String script = "tell application \"System Preferences\"\n" +
        "  reveal pane id \"com.apple.preference.security\"\n" +
        "end tell\n" +
        "tell application \"System Preferences\" to activate";
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("osascript", "-e", script);
      processBuilder.start();
    } catch (Exception e) {
      e.getStackTrace();
    }
  }

  @Override
  public boolean isIdle() {
    String appleScript = "set idleTime to (do shell script \"ioreg -c IOHIDSystem | awk '/HIDIdleTime/ {print $NF/1000000; exit}'\")\n"
        +
        "return idleTime";
    try {
      // Construct the command to run AppleScript
      String[] command = { "osascript", "-e", appleScript };
      Process process = Runtime.getRuntime().exec(command);

      // Read the output of the AppleScript
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String idleTimeStr = reader.readLine();

      // Convert idle time to a numeric value (in milliseconds)
      if (idleTimeStr != null) {
        double idleTime = Double.parseDouble(idleTimeStr.trim());

        // Check if idle time is >= 30 seconds (30000 ms)
        return idleTime >= 30000;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Return false if there is an error or invalid output
    return false;
  }

  @Override
  public boolean isSuspended() {
    try {
      // Run the pmset command
      String[] command = { "pmset", "-g", "ps" };
      Process process = Runtime.getRuntime().exec(command);

      // Read the output of the command
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        // Check for sleep indicators
        if (line.toLowerCase().contains("sleep")) {
          return true;
        }
      }
      process.waitFor();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false; // Default to false if no indication is found
  }

  @Override
  public boolean isLocked() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isLocked'");
  }

  @Override
  public boolean isLidClosed() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isLidClosed'");
  }

  @Override
  public boolean stateChangeIsSame(JSONObject last, JSONObject current) {
    if (last == null || current == null) {
      return last == current;
    }

    boolean namesMatch = current.optString("name").equals(last.optString("name"));
    boolean titlesMatch = current.optString("title").equals(last.optString("title"));
    boolean urlsMatch = current.optString("url").equals(last.optString("url"));

    return namesMatch && titlesMatch && urlsMatch;
  }

  @Override
  public String getAppName(String payload) {
    return extractValue(payload);
  }

  @Override
  public String getAppTitle(String payload) {
    return extractValue(payload);
  }

  // @Override
  // public String getAppUrl(String appName, String payload) {
  // return extractValue(payload);
  // }

  /**
   * This is a demo version of the getAppUrl that leverages websockets to
   * communicate with
   * the browser extension to retreive the url when the given app is a browser
   * this is natively supported on macos and will only be needed on windows and
   * linux
   **/
  @Override
  public String getAppUrl(String appName, String payload) {
    if (!"Application".equals(appName)) {
      JSONObject request = new JSONObject();
      String requestId = UUID.randomUUID().toString(); // Generate a unique ID for the request
      request.put("action", "request");
      request.put("channel", "GET_CURRENT_URL"); // Channel to request the URL
      request.put("payload", "Requesting current URL");
      request.put("requestId", requestId); // Add the requestId to the request

      String channel = appName + ".GET_CURRENT_URL";

      // Send the request and wait for the response
      CompletableFuture<String> futureResponse = this.websocket.broadcastToChannelSync(channel,
          request.toString());
      try {
        long timeoutMillis = 1000; // 1 seconds timeout
        String currentUrl = futureResponse.get(timeoutMillis, TimeUnit.MILLISECONDS);
        this.logManager.debug("Received current URL: " + currentUrl);
        return currentUrl;
      } catch (Exception e) {
        this.logManager.error("Error getting current URL: " + e.getMessage());
      }
    }
    return extractValue(payload);
  }

  private JSONObject getInFocusApplication() {
    String script = "tell application \"System Events\"\n" +
        "    -- Get the frontmost application's name\n" +
        "    set frontmostApp to name of first application process whose frontmost is true\n" +
        "    \n" +
        "    -- Try to get the title of the front window (for supported apps)\n" +
        "    try\n" +
        "        set tabTitle to name of front window of application process frontmostApp\n" +
        "    on error\n" +
        "        -- If the window title cannot be retrieved, provide a default message\n" +
        "        set tabTitle to \"No title available\"\n" +
        "    end try\n" +
        "end tell\n" +
        "if frontmostApp is \"Safari\" then\n" +
        "    tell application \"Safari\"\n" +
        "        set tabTitle to name of front tab of window 1\n" +
        "        set tabURL to URL of front tab of window 1\n" +
        "    end tell\n" +
        "    return \"Browser: Safari<BREAK> Title: \" & tabTitle & \"<BREAK> URL: \" & tabURL\n" +
        "else if frontmostApp is \"Google Chrome\" then\n" +
        "    tell application \"Google Chrome\"\n" +
        "        set tabTitle to title of active tab of window 1\n" +
        "        set tabURL to URL of active tab of window 1\n" +
        "    end tell\n" +
        "    return \"Browser: Google Chrome<BREAK> Title: \" & tabTitle & \"<BREAK> URL: \" & tabURL\n" +
        "else if frontmostApp is \"Firefox\" then\n" +
        "    return \"Browser: Firefox<BREAK> Title: Firefox<BREAK>URL: Firefox\"\n" +
        "else\n" +
        "    return \"Application: \" & frontmostApp & \"<BREAK>Title: \" & tabTitle\n" +
        "end if";
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("osascript", "-e", script);
      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder result = new StringBuilder();
      String line;

      while ((line = reader.readLine()) != null) {
        result.append(line).append("\n");
      }

      BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      StringBuilder errorResult = new StringBuilder();
      while ((line = errorReader.readLine()) != null) {
        errorResult.append(line).append("\n");
      }

      String output = result.toString().trim();
      String errors = errorResult.toString().trim();

      if (!errors.isEmpty()) {
        this.logManager.error(errors);
      }

      String[] parts = output.split("<BREAK>");
      JSONObject activity = new JSONObject();
      String appName = this.getAppName(parts[0]);
      String appTitle = this.getAppTitle(parts[1]);
      activity.put("name", appName);
      activity.put("title", appTitle);
      if (output.startsWith("Browser")) {
        activity.put("url", this.getAppUrl(appName, parts[2]));
      } else {
        activity.put("url", this.getAppUrl("Application", parts[1]));
      }
      return activity;
    } catch (Exception e) {
      this.logManager.error(e.getMessage());
    }
    return null;
  }

  @Override
  public JSONObject getCurrentSystemState() {
    return getInFocusApplication();
  }

  @Override
  public void run() {
    if (!hasPermissions()) {
      requestPermissions();
      return;
    }
    JSONObject lastEvent = null;
    while (true) {
      if (isIdle()) {
        this.logManager.debug("System idle");
        continue;
      }
      JSONObject currentEvent = getCurrentSystemState();
      if (currentEvent != null && !stateChangeIsSame(lastEvent, currentEvent)) {
        LocalDateTime now = LocalDateTime.now();
        currentEvent.put("startTime", now.toString());

        // perform app discoverability here
        this.appService.create(currentEvent.getString("name"));

        // Set end time and duration for the last event if it exists
        if (lastEvent != null) {
          lastEvent.put("endTime", now.toString()); // Set end time for the last event
          this.activityService.upsert(lastEvent);
        }

        this.logManager.debug("Activity changed: %s\n->\n%s".formatted(lastEvent, currentEvent));
        this.activityService.upsert(currentEvent);
        lastEvent = currentEvent;
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        this.logManager.debug("Sleep interrupted: %s".formatted(e.getMessage()));
      }
    }
  }
}
