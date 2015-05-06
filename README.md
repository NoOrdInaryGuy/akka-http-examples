# akka-http-reactive-mongo
Experimentation with Akka HTTP and ReactiveMongo

* akka-http and streams isn't yet fully documented with examples.
* This repository has examples of processing HTTP requests with handlers and Flows on the server side. It also has examples of using the HTTP client API to exercise the server side.

* Data taken from: http://jsonstudio.com/wp-content/uploads/2014/02/stocks.zip
* ```cat data/stocks.json | mongoimport --db akka --collection stocks```
* The version in master is based on akka-http 1.0-RC2 - see other branch for akka-http 1.0-M2 level example.
* See also: http://www.smartjava.org/content/building-rest-service-scala-akka-http-akka-streams-and-reactive-mongo
