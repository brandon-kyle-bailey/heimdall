const socket = new WebSocket("ws://localhost:8080");

// const IS_CHROMIUM = navigator.userAgent.includes("Chrome");

socket.addEventListener("open", () => {
  console.log("Connected to WebSocket server");
  // Example mutation
  socket.send(
    JSON.stringify({
      action: "mutation",
      channel: "CREATE_USER",
      payload: JSON.stringify({ userId: "test", accountId: "test" }),
    }),
  );
});
