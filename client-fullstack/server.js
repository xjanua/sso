const http = require('http');
const fs = require('fs');
const path = require('path');

const portArgument = process.argv.find((argument) => argument.startsWith('--port='));
const port = Number(portArgument?.split('=')[1] || process.env.PORT || 3000);
const publicDirectory = __dirname;

const contentTypes = {
  '.css': 'text/css; charset=utf-8',
  '.html': 'text/html; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
};

const server = http.createServer((request, response) => {
  const pathname = new URL(request.url, `http://${request.headers.host}`).pathname;
  const requestedFile = pathname === '/style.css'
    ? 'style.css'
    : pathname === '/app.js'
      ? 'app.js'
      : 'index.html';
  const filePath = path.join(publicDirectory, requestedFile);

  fs.readFile(filePath, (error, content) => {
    if (error) {
      response.writeHead(500, { 'Content-Type': 'text/plain; charset=utf-8' });
      response.end('Không thể tải Client App.');
      return;
    }

    response.writeHead(200, {
      'Content-Type': contentTypes[path.extname(filePath)],
      'Cache-Control': 'no-store',
    });
    response.end(content);
  });
});

server.listen(port, () => {
  console.log(`Client App: http://localhost:${port}`);
  console.log(`Callback URL: http://localhost:${port}/auth/callback`);
});
