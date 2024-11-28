const socket = new WebSocket("ws://localhost:8080");

const userAgent = navigator.userAgent;

function getBrowserName() {
  if (
    userAgent.includes("Chrome") &&
    !userAgent.includes("Edge") &&
    !userAgent.includes("Brave")
  ) {
    return "Google Chrome";
  } else if (userAgent.includes("Firefox")) {
    return "Firefox";
  } else if (userAgent.includes("Safari") && !userAgent.includes("Chrome")) {
    return "Safari";
  } else if (userAgent.includes("Edge")) {
    return "Microsoft Edge";
    // } else if (userAgent.includes("MSIE") || userAgent.includes("Trident")) {
    //   return "Internet Explorer";
  } else {
    return "Unknown Browser";
  }
}

const browserName = getBrowserName();

socket.addEventListener("open", () => {
  console.log("Connected to WebSocket server");
  socket.send(
    JSON.stringify({
      action: "subscribe",
      channel: `${browserName}.GET_CURRENT_URL`,
    }),
  );

  socket.addEventListener("message", (event) => {
    try {
      const message = JSON.parse(event.data);
      if (
        message.action === "request" &&
        message.channel === `${browserName}.GET_CURRENT_URL`
      ) {
        const currentUrl = window.location.href; // Get current tab URL
        const payload = {
          action: "response",
          requestId: message.requestId,
          payload: currentUrl,
        };
        socket.send(JSON.stringify(payload));
      }
    } catch (error) {
      console.log(error, event.data);
    }
  });
});
