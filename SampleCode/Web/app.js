const Koa = require('koa')
const http = require("http");
const app = new Koa()

app.use(require('koa-static')(__dirname))
http.createServer(app.callback()).listen(3001);