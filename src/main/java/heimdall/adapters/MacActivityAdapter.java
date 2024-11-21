package heimdall.adapters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

import org.json.JSONObject;

import heimdall.common.enums.EDomainEvents;
import heimdall.common.interfaces.IActivityTracker;
import heimdall.ports.LoggerPort;

public class MacActivityAdapter implements IActivityTracker {

  private EventbusAdapter _eventbus;

  public MacActivityAdapter(EventbusAdapter eventbus) {
    this._eventbus = eventbus;
  }

  private String extractValue(String part) {
    String[] splitPart = part.split(": ");
    if (splitPart.length > 1) {
      return splitPart[1].trim(); // Return the value after ": "
    }
    return "Unknown"; // Default value in case of unexpected format
  }

  private JSONObject executeFrontProcessAppleScript() {
    String script = "tell application \"System Events\"\n" +
        "    -- Get the frontmost application's name\n" +
        "    set frontmostApp to name of first application process whose frontmost is true\n" +
        "\n" +
        "    -- Try to get the title of the front window (for supported apps)\n" +
        "    try\n" +
        "        set tabTitle to name of front window of application process frontmostApp\n" +
        "    on error\n" +
        "        -- If the window title cannot be retrieved, provide a default message\n" +
        "        set tabTitle to \"No title available\"\n" +
        "    end try\n" +
        "end tell\n" +
        "\n" +
        "if frontmostApp is \"Safari\" then\n" +
        "    tell application \"Safari\"\n" +
        "        set tabTitle to name of current tab of window 1\n" +
        "        set tabURL to URL of current tab of window 1\n" +
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
        "        set tabTitle to title of active tab of window 1\n" +
        "        set tabURL to URL of active tab of window 1\n" +
        "    end tell\n" +
        "    return \"Browser: Firefox<BREAK> Title: \" & tabTitle & \"<BREAK> URL: \" & tabURL\n" +
        "else\n" +
        "    -- Handle unsupported applications\n" +
        "    return \"Application: \" & frontmostApp & \"<BREAK> Title: \" & tabTitle\n" +
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
        LoggerPort.error("AppleScript Error: %s".formatted(errors));
      }

      String[] parts = output.split("<BREAK>");
      JSONObject activity = new JSONObject();
      if (output.startsWith("Browser")) {
        activity.put("name", extractValue(parts[0]));
        activity.put("title", extractValue(parts[1]));
        activity.put("url", extractValue(parts[2]));
      } else if (output.startsWith("Application")) {
        activity.put("name", extractValue(parts[0]));
        activity.put("title", extractValue(parts[1]));
        activity.put("url", extractValue(parts[1]));
      }
      return activity;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean hasPermissions() {
    try {
      // Simple check for accessibility permissions
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
      LoggerPort.error(e.getMessage());
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
      e.printStackTrace();
    }
  }

  @Override
  public boolean isIdle() {
    return false;
  }

  @Override
  public boolean isSuspended() {
    return false;
  }

  @Override
  public boolean isLidClosed() {
    return false;
  }

  @Override
  public Object getActiveWindow() {
    return null;
  }

  @Override
  public void run() {
    if (!hasPermissions()) {
      requestPermissions();
      return;
    }
    LoggerPort.debug("Hello world from mac activity tracker");

    JSONObject lastActivity = null; // Track the last focused window
    while (true) {
      JSONObject currentActivity = this.executeFrontProcessAppleScript();
      if (currentActivity != null &&
          !currentActivity.equals(lastActivity)) {
        LoggerPort.debug("Activity changed: %s".formatted(currentActivity));
        this._eventbus.publish(EDomainEvents.CREATE_APP.toString(), currentActivity);
        if (lastActivity != null) {
          lastActivity.put("endTime", LocalDateTime.now());
          this._eventbus.publish(EDomainEvents.UPSERT_ACTIVITY.toString(), lastActivity);
        }
        currentActivity.put("startTime", LocalDateTime.now());
        this._eventbus.publish(EDomainEvents.UPSERT_ACTIVITY.toString(), currentActivity);
        lastActivity = currentActivity;
        try {
          Thread.sleep(100); // Sleep for 100 milliseconds
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
