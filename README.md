# akka-http-examples
Experimentation with Akka HTTP, Streams, and ReactiveMongo

* akka-http and streams isn't yet fully documented with examples, so this is what I've deduced so far.
* This repository has examples of:
	1. Processing HTTP requests with handlers: ```BootWithHandler```
	2. Processing HTTP requests with Flows: ```BootWithFlow```
	3. Akka's Routing DSL: ```BootWithRouting```
	4. Akka's Routing DSL, to hook WebSockets up to Flows: ```BootWithRoutingWebSockets```
	5. Using the HTTP client API to exercise the server side: ```Client``` 
* The version in master is based on akka-http 1.0-RC3 - see other branch for akka-http 1.0-M2 level example.

### Setup
* ```cat data/stocks.json | mongoimport --db akka --collection stocks```

### Notes
* Data taken from: http://jsonstudio.com/wp-content/uploads/2014/02/stocks.zip
* See also: http://www.smartjava.org/content/building-rest-service-scala-akka-http-akka-streams-and-reactive-mongo
 (examples for the 1.0-M2 release that inspired this repo). 
* See also: http://doc.akka.io/docs/akka-stream-and-http-experimental/1.0-RC2/scala/http/server.html
* Use http://www.websocket.org/echo.html for WebSocket experimentation.
