package heimdall.infrastructure.adapters.system.macos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

import org.json.JSONObject;

import heimdall.common.enums.EDomainEvents;
import heimdall.common.interfaces.IActivityTracker;
import heimdall.infrastructure.adapters.events.EventbusAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.dtos.activity.UpsertActivityDto;
import heimdall.interfaces.dtos.app.CreateAppDto;

public class MacosActivityAdapter implements IActivityTracker {

  private final LoggingPort logManager;
  private final EventbusAdapter _eventbus;

  public MacosActivityAdapter(LoggingPort logManager, EventbusAdapter eventbus) {
    this.logManager = logManager;
    this._eventbus = eventbus;
  }

  private String extractValue(String part) {
    String[] splitPart = part.split(": ");
    if (splitPart.length > 1) {
      return splitPart[1].trim();
    }
    return "Unknown";
  }

  private JSONObject executeFrontProcessAppleScript() {
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
        "    tell application \"Firefox\"\n" +
        "        set tabTitle to title of front window\n" +
        "        set tabURL to URL of front window\n" +
        "    end tell\n" +
        "    return \"Browser: Firefox<BREAK> Title: \" & tabTitle & \"<BREAK> URL: \" & tabURL\n" +
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
        this.logManager.error("AppleScript Error: %s".formatted(errors));
      }

      String[] parts = output.split("<BREAK>");
      JSONObject activity = new JSONObject();
      if (output.startsWith("Browser")) {
        activity.put("name", this.getApp(parts[0]));
        activity.put("title", this.getTitle(parts[1]));
        activity.put("url", this.getUrl(parts[2]));
        // TODO... this is supported out of the box by mac. dont rely on websocket for
        // mac
        // activity.put("url", this.getUrl(parts[2]));
        String dummyUrl = this.getUrl(parts[0]);
        this.logManager.debug("DUMMY URL %s".formatted(dummyUrl));
        activity.put("url", this.extractValue(parts[2]));
      } else if (output.startsWith("Application")) {
        activity.put("name", this.getApp(parts[0]));
        activity.put("title", this.getTitle(parts[1]));
        activity.put("url", this.extractValue(parts[1]));

      }
      return activity;
    } catch (Exception e) {
      this.logManager.error("Error executing AppleScript: %s".formatted(e.getMessage()));
    }
    return null;
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
      this.logManager.error("Permission check failed: %s".formatted(e.getMessage()));
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
      this.logManager.error("Failed to request permissions: %s".formatted(e.getMessage()));
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
  public boolean isLidClosed() {
    try {
      // Run the pmset command
      String[] command = { "pmset", "-g", "ps" };
      Process process = Runtime.getRuntime().exec(command);

      // Read the output of the command
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        // Check for lid-closed indicators
        if (line.toLowerCase().contains("lid")) {
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
  public JSONObject getActiveWindow() {
    if (!this.isIdle() && !this.isLidClosed() && !this.isSuspended()) {
      this.logManager.debug("Executing apple script");
      return this.executeFrontProcessAppleScript();
    }
    this.logManager.debug("Machine is either idle, lid closed or suspended");
    return null;
  }

  @Override
  public boolean currentActivityEqualsLastActivity(JSONObject currentActivity, JSONObject lastActivity) {
    if (currentActivity == null || lastActivity == null) {
      return currentActivity == lastActivity;
    }

    boolean namesMatch = currentActivity.optString("name").equals(lastActivity.optString("name"));
    boolean titlesMatch = currentActivity.optString("title").equals(lastActivity.optString("title"));
    boolean urlsMatch = currentActivity.optString("url").equals(lastActivity.optString("url"));

    return namesMatch && titlesMatch && urlsMatch;
  }

  @Override
  public void run() {
    if (!hasPermissions()) {
      requestPermissions();
      return;
    }
    JSONObject lastActivity = null;
    while (true) {
      this.logManager.debug("are we still logging?");
      JSONObject currentActivity = this.getActiveWindow();
      if (currentActivity != null && !currentActivityEqualsLastActivity(currentActivity, lastActivity)) {
        this.logManager.debug("Activity changed: %s -> %s".formatted(lastActivity, currentActivity));

        CreateAppDto createAppDto = new CreateAppDto(currentActivity.getString("name"));
        this._eventbus.publish(EDomainEvents.COMMAND_CREATE_APP.toString(), createAppDto);

        if (lastActivity != null) {
          lastActivity.put("endTime", LocalDateTime.now().toString());
          UpsertActivityDto upsertActivityDto = new UpsertActivityDto(
              lastActivity.getString("name"),
              lastActivity.getString("title"),
              lastActivity.getString("url"),
              lastActivity.has("startTime") ? LocalDateTime.parse(lastActivity.getString("startTime")) : null,
              lastActivity.has("endTime") ? LocalDateTime.parse(lastActivity.getString("endTime")) : null,
              lastActivity.has("duration") ? lastActivity.getInt("duration") : null);
          this._eventbus.publish(EDomainEvents.COMMAND_UPSERT_ACTIVITY.toString(), upsertActivityDto);
        }

        currentActivity.put("startTime", LocalDateTime.now().toString());
        UpsertActivityDto upsertActivityDto = new UpsertActivityDto(
            currentActivity.getString("name"),
            currentActivity.getString("title"),
            currentActivity.getString("url"),
            currentActivity.has("startTime") ? LocalDateTime.parse(currentActivity.getString("startTime")) : null,
            currentActivity.has("endTime") ? LocalDateTime.parse(currentActivity.getString("endTime")) : null,
            currentActivity.has("duration") ? currentActivity.getInt("duration") : null);
        this._eventbus.publish(EDomainEvents.COMMAND_UPSERT_ACTIVITY.toString(), upsertActivityDto);

        lastActivity = currentActivity;

        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          this.logManager.error("Sleep interrupted: %s".formatted(e.getMessage()));
        }
      }
    }
  }

  @Override
  public String getApp(String payload) {
    return this.extractValue(payload);
  }

  @Override
  public String getTitle(String payload) {
    return this.extractValue(payload);
  }

  @Override
  public String getUrl(String payload) {
    String appName = this.extractValue(payload);
    this.logManager.debug("GETTING URL FOR APPNAME %s".formatted(appName));
    JSONObject messageObj = new JSONObject();
    messageObj.put("channel", appName);
    messageObj.put("payload", "this is a test");
    // String url = this._websocket.broadcastToChannelSync(appName,
    // messageObj.toString());
    // this.logManager.debug("URL %s".formatted(url));
    return this.extractValue(payload);
  }
}
