const net = require("net");

const client = net.createConnection({ port: 12345 }, () => {
  console.log("Connected to the server");
  client.write(
    JSON.stringify({
      event: "REGISTER_USER",
      payload: {
        userId: "this-is-a-user-id",
        accountId: "this-is-an-account-id",
      },
    }),
  );
  client.end();
});

// client.on("data", (data) => {
//   console.log("Received from server:", data.toString());
// });
