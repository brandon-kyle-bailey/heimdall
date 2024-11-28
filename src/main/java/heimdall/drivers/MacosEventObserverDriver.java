
package heimdall.drivers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import heimdall.adapters.WebsocketAdapter;
import heimdall.common.interfaces.IEventObserverDriver;
import heimdall.ports.LoggingPort;
import heimdall.services.ActivityService;
import heimdall.services.AppWatcherService;

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
    try {
      String[] command = { "pmset", "-g", "assertions" };
      Process process = Runtime.getRuntime().exec(command);

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      boolean isBlockingIdle = false;

      while ((line = reader.readLine()) != null) {
        if (line.contains("PreventUserIdleDisplaySleep") && line.contains("1")) {
          isBlockingIdle = true;
        }
      }

      if (isBlockingIdle) {
        return false;
      }

      String appleScript = "set idleTime to (do shell script \"ioreg -c IOHIDSystem | awk '/HIDIdleTime/ {print $NF/1000000; exit}'\")\n"
          + "return idleTime";
      String[] idleCommand = { "osascript", "-e", appleScript };
      Process idleProcess = Runtime.getRuntime().exec(idleCommand);
      BufferedReader idleReader = new BufferedReader(new InputStreamReader(idleProcess.getInputStream()));
      String idleTimeStr = idleReader.readLine();

      if (idleTimeStr != null) {
        double idleTime = Double.parseDouble(idleTimeStr.trim());
        return idleTime >= 30000;
      }
    } catch (Exception e) {
      this.logManager.error(e.getMessage());
    }

    return false;
  }

  @Override
  public boolean isSuspended() {
    try {
      String[] pmsetCommand = { "pmset", "-g", "assertions" };
      Process pmsetProcess = Runtime.getRuntime().exec(pmsetCommand);
      BufferedReader pmsetReader = new BufferedReader(new InputStreamReader(pmsetProcess.getInputStream()));

      boolean systemSleepAllowed = true;
      boolean displaySleepAllowed = true;

      String line;
      while ((line = pmsetReader.readLine()) != null) {
        if (line.contains("PreventUserIdleDisplaySleep") && line.contains("1")) {
          displaySleepAllowed = false;
        }
        if (line.contains("PreventUserIdleSystemSleep") && line.contains("1")) {
          systemSleepAllowed = false;
        }
      }
      return systemSleepAllowed && displaySleepAllowed;
    } catch (Exception e) {
      this.logManager.error(e.getMessage());
    }

    return false;
  }

  @Override
  public boolean isLocked() {
    try {
      String screensaverCheck = "tell application \"System Events\" to return screen saver running";
      String[] appleScriptCommand = { "osascript", "-e", screensaverCheck };
      Process appleScriptProcess = Runtime.getRuntime().exec(appleScriptCommand);
      BufferedReader appleScriptReader = new BufferedReader(new InputStreamReader(appleScriptProcess.getInputStream()));
      boolean screensaverRunning = false;

      String line;
      if ((line = appleScriptReader.readLine()) != null && line.equals("true")) {
        screensaverRunning = true;
      }

      String loginCheck = "tell application \"System Events\" to return (exists (window 1 of process \"loginwindow\"))";
      String[] appleLoginCheckCommand = { "osascript", "-e", loginCheck };
      Process appleLoginProcess = Runtime.getRuntime().exec(appleLoginCheckCommand);
      BufferedReader appleLoginReader = new BufferedReader(new InputStreamReader(appleLoginProcess.getInputStream()));
      boolean loginWindowVisible = false;

      if ((line = appleLoginReader.readLine()) != null && line.equals("true")) {
        loginWindowVisible = true;
      }
      return screensaverRunning || loginWindowVisible;
    } catch (Exception e) {
      this.logManager.error(e.getMessage());
    }
    return false;
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

  /**
   * Must rely on bifrost to retrieve the tab title and url of the following
   * browsers:
   * - firefox
   **/
  @Override
  public String getAppTitle(String appName, String payload) {
    // if were dealing with a browser and that browser is firefox:
    // reach out to bifrost to get the tab title and url
    if (!"Application".equals(appName) && appName.equals("Firefox")) {
      JSONObject request = new JSONObject();
      String requestId = UUID.randomUUID().toString(); // Generate a unique ID for the request
      String channel = appName + ".GET_CURRENT_TAB";
      request.put("action", "request");
      request.put("channel", channel); // Channel to request the URL
      request.put("payload", "Requesting current TAB");
      request.put("requestId", requestId); // Add the requestId to the request

      // Send the request and wait for the response
      CompletableFuture<String> futureResponse = this.websocket.broadcastToChannelSync(channel,
          request.toString());
      try {
        long timeoutMillis = 1000; // 1 seconds timeout
        String currentTitle = futureResponse.get(timeoutMillis, TimeUnit.MILLISECONDS);
        // this.logManager.debug("Received current URL: " + currentUrl);
        // fallback to the payload if no title can be found
        return currentTitle != null ? currentTitle : extractValue(payload);
      } catch (Exception e) {
        this.logManager.error("Error getting current browser tab title: " + e.getMessage());
      }
    }
    return extractValue(payload);
  }

  /**
   * Must rely on bifrost to retrieve the tab title and url of the following
   * browsers:
   * - firefox
   **/
  @Override
  public String getAppUrl(String appName, String payload) {
    // if were dealing with a browser and that browser is firefox:
    // reach out to bifrost to get the tab title and url
    if (!"Application".equals(appName) && appName.equals("Firefox")) {
      JSONObject request = new JSONObject();
      String requestId = UUID.randomUUID().toString(); // Generate a unique ID for the request
      String channel = appName + ".GET_CURRENT_URL";
      request.put("action", "request");
      request.put("channel", channel); // Channel to request the URL
      request.put("payload", "Requesting current URL");
      request.put("requestId", requestId); // Add the requestId to the request

      // Send the request and wait for the response
      CompletableFuture<String> futureResponse = this.websocket.broadcastToChannelSync(channel,
          request.toString());
      try {
        long timeoutMillis = 1000; // 1 seconds timeout
        String currentUrl = futureResponse.get(timeoutMillis, TimeUnit.MILLISECONDS);
        // this.logManager.debug("Received current URL: " + currentUrl);
        // fallback to the payload if no title can be found
        return currentUrl != null ? currentUrl : extractValue(payload);
      } catch (Exception e) {
        this.logManager.error("Error getting current browser url: " + e.getMessage());
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
      activity.put("name", appName);
      if (output.startsWith("Browser")) {
        activity.put("title", this.getAppTitle(appName, parts[1]));
        activity.put("url", this.getAppUrl(appName, parts[2]));
      } else {
        activity.put("title", this.getAppTitle("Application", parts[1]));
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
      if (isLocked()) {
        this.logManager.debug("System locked");
        continue;
      }
      if (isSuspended()) {
        this.logManager.debug("System suspended");
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
