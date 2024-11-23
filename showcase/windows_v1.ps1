Add-Type @"
using System;
using System.Runtime.InteropServices;

public class User32 {
    [DllImport("user32.dll")]
    public static extern IntPtr GetForegroundWindow();

    [DllImport("user32.dll", SetLastError = true)]
    [return: MarshalAs(UnmanagedType.Bool)]
    public static extern bool GetWindowText(IntPtr hWnd, System.Text.StringBuilder lpString, int nMaxCount);

    [DllImport("user32.dll", SetLastError = true)]
    public static extern int GetWindowTextLength(IntPtr hWnd);

    [DllImport("user32.dll")]
    public static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint lpdwProcessId);
}
"@

function Get-ActiveWindowTitle {
    $handle = [User32]::GetForegroundWindow()
    if ($handle -eq [IntPtr]::Zero) {
        return "No active window"
    }

    $length = [User32]::GetWindowTextLength($handle)
    $title = New-Object System.Text.StringBuilder ($length + 1)
    [User32]::GetWindowText($handle, $title, $title.Capacity)
    return $title.ToString()
}

function Get-ProcessNameFromHandle {
    param ($handle)
    [uint32]$processId = 0
    $null = [User32]::GetWindowThreadProcessId($handle, [ref]$processId)
    if ($processId -ne 0) {
        $process = Get-Process -Id $processId
        return $process.Name
    } else {
        return $null
    }
}

function Get-ChromeURL {
    return "Unknown"
}

function Get-EdgeURL {
    return "Unknown"
}

function Get-FirefoxURL {
    return "Unknown"
}

# while ($true) {
    $handle = [User32]::GetForegroundWindow()
    $frontmostApp = Get-ProcessNameFromHandle -handle $handle
    $tabTitle = Get-ActiveWindowTitle
    switch ($frontmostApp) {
        "chrome" {
            $tabURL = Get-ChromeURL
            Write-Output "Browser: Google Chrome, Title: $tabTitle, URL: $tabURL"
        }
        "firefox" {
            $tabURL = Get-FirefoxURL
            Write-Output "Browser: Firefox, Title: $tabTitle, URL: $tabURL"
        }
        "msedge" {
            $tabURL = Get-EdgeURL
            Write-Output "Browser: Microsoft Edge, Title: $tabTitle, URL: $tabURL"
        }
        default {
            Write-Output "Application: $frontmostApp<BREAK>Title: $tabTitle"
        }
    }

    # Start-Sleep -Seconds 1
# }
