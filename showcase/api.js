const http = require("http");
const url = require("url");

const port = 3000;

const server = http.createServer((req, res) => {
  const parsedUrl = url.parse(req.url, true);

  if (req.method === "GET" && parsedUrl.pathname === "/api/message") {
    res.writeHead(200, { "Content-Type": "application/json" });
    res.end(JSON.stringify({ message: "Hello, world!" }));
  } else if (req.method === "POST" && parsedUrl.pathname === "/api/message") {
    let body = "";

    req.on("data", (chunk) => {
      body += chunk;
    });

    req.on("end", () => {
      try {
        const parsedBody = JSON.parse(body);
        if (parsedBody.text) {
          res.writeHead(200, { "Content-Type": "application/json" });
          res.end(JSON.stringify({ received: parsedBody.text }));
        } else {
          res.writeHead(400, { "Content-Type": "application/json" });
          res.end(JSON.stringify({ error: "No text provided" }));
        }
      } catch (e) {
        res.writeHead(400, { "Content-Type": "application/json" });
        console.log(e);
        res.end(JSON.stringify({ error: "Invalid JSON" }));
      }
    });
  } else {
    res.writeHead(404, { "Content-Type": "application/json" });
    res.end(JSON.stringify({ error: "Not Found" }));
  }
});

server.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
