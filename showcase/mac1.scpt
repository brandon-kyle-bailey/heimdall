tell application "System Events"
    -- Get the frontmost application's name
    set frontmostApp to name of first application process whose frontmost is true

    -- Try to get the title of the front window (for supported apps)
    try
        set tabTitle to name of front window of application process frontmostApp
    on error
        -- If the window title cannot be retrieved, provide a default message
        set tabTitle to "No title available"
    end try
end tell
if frontmostApp is "Safari" then
    tell application "Safari"
        set tabTitle to name of front tab of window 1
        set tabURL to URL of front tab of window 1
    end tell
    return "Browser: Safari, Title: " & tabTitle & ", URL: " & tabURL
else if frontmostApp is "Google Chrome" then
    tell application "Google Chrome"
        set tabTitle to title of active tab of window 1
        set tabURL to URL of active tab of window 1
    end tell
    return "Browser: Google Chrome, Title: " & tabTitle & ", URL: " & tabURL
else if frontmostApp is "Firefox" then
    tell application "Firefox"
        set tabTitle to title of front window
        set tabURL to URL of front window
    end tell
    return "Browser: Firefox, Title: " & tabTitle & ", URL: " & tabURL
else
    return "Application: " & frontmostApp & "<BREAK>Title: " & tabTitle
end if
