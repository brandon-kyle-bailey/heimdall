const net = require("net");

const client = net.createConnection({ port: 8080 }, () => {
  console.log("Connected to the server");
  client.write(
    JSON.stringify({
      event: "REGISTER_USER",
      payload: {
        userId: "some-random-user-id",
        accountId: "some-random-account-id",
      },
    }),
  );
  console.log("Message written");
  client.end();
});

// client.on("data", (data) => {
//   console.log("Received from server:", data.toString());
// });
