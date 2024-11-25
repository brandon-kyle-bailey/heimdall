const socket = new WebSocket("ws://localhost:8080");

socket.addEventListener("open", () => {
  console.log("WebSocket connection established.");

  // Subscribe to the "Google Chrome" channel
  socket.send(
    JSON.stringify({
      action: "subscribe",
      channel: "Google Chrome",
    }),
  );
});

socket.addEventListener("message", (event) => {
  console.log("Received event from WebSocket server:", event.data);

  try {
    const message = JSON.parse(event.data);

    // Check if the message is a broadcast for a channel
    if (message.channel === "Google Chrome" && message.payload) {
      // Respond to the server with `window.location.href`
      const currentLocation = window.location.href;
      console.log(`Responding with URL: ${currentLocation}`);

      socket.send(
        JSON.stringify({
          action: "publish", // Respond to the server
          channel: "Google Chrome", // Keep the same channel
          payload: JSON.stringify({
            url: currentLocation,
          }),
        }),
      );
    }
  } catch (error) {
    console.error("Failed to handle server message:", error);
  }
});

// Optionally handle connection errors or closures
socket.addEventListener("close", () => {
  console.log("WebSocket connection closed.");
});

socket.addEventListener("error", (error) => {
  console.error("WebSocket error:", error);
});
