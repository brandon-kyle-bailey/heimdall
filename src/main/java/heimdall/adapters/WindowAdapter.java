package heimdall.adapters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.io.IOException;

import common.shared.inter.IWindowInfo;
import heimdall.adapters.models.BrowserWindowInfo;
import heimdall.adapters.models.DesktopWindowInfo;

public class WindowAdapter {

  // AppleScript to open the Accessibility Preferences pane
  private static void openAccessibilityPreferences() {
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

  // AppleScript to get the title of the focused window
  private static IWindowInfo getActiveWindow() {
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
        System.out.println("AppleScript Error: " + errors);
      }

      String[] parts = output.split("<BREAK>");

      // System.out.println(Arrays.toString(parts));
      if (output.startsWith("Browser")) {
        String browserName = parts[0].split(": ")[1];
        String tabTitle = parts[1].split(": ")[1];
        String tabURL = parts[2].split(": ")[1];
        return new BrowserWindowInfo(browserName, tabTitle, tabURL);
      } else if (output.startsWith("Application")) {
        String applicationName = parts[0].split(": ")[1];
        String tabTitle = parts[1].split(": ")[1];
        // System.out.println(Arrays.toString(parts));
        return new DesktopWindowInfo(applicationName, tabTitle); // Use
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  // Check if the app has accessibility permissions (simple test via System
  // Events)
  private static boolean hasAccessibilityPermissions() {
    try {
      String script = "tell application \"System Events\" to return (exists (process 1))"; // Simple check for
                                                                                           // accessibility permissions
      ProcessBuilder processBuilder = new ProcessBuilder("osascript", "-e", script);
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        if ("true".equals(line.trim())) {
          return true; // Accessibility permissions are enabled
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false; // Accessibility permissions are not enabled or something went wrong
  }

  public IWindowInfo getFocusedWindow() {
    IWindowInfo windowInfo = WindowAdapter.getActiveWindow();
    if (windowInfo != null) {
      return windowInfo;
    }
    return null;
  }

  public void run() {
    // Check if the app has accessibility permissions
    if (!hasAccessibilityPermissions()) {
      WindowAdapter.openAccessibilityPreferences();
      return;
    }

    IWindowInfo lastFocusedWindow = null; // Track the last focused window
    while (true) {
      IWindowInfo currentFocusedWindow = WindowAdapter.getActiveWindow(); // Get the current focused window

      if (currentFocusedWindow != null && !currentFocusedWindow.equals(lastFocusedWindow)) {
        // Log only if the focused window has changed
        System.out.println("Focused window changed: " + currentFocusedWindow);
        // Update last focused window
        lastFocusedWindow = currentFocusedWindow;
        System.out.println("Application Name: " + currentFocusedWindow.getApplicationName());
        System.out.println("Window Title: " + currentFocusedWindow.getWindowTitle());
        System.out.println("Additional Info: " + currentFocusedWindow.getAdditionalInfo());
        System.out.println("\n");
      }

      try {
        Thread.sleep(100); // Sleep for 100 milliseconds
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
