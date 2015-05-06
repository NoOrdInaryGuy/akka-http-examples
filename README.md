# akka-http-reactive-mongo
Experimentation with Akka HTTP and ReactiveMongo

* Data taken from: http://jsonstudio.com/wp-content/uploads/2014/02/stocks.zip
* ```cat data/stocks.json | mongoimport --db akka --collection stocks```
* The version in master is based on akka-http 1.0-RC2 - see other branch for akka-http 1.0-M2 level example.
* See also: http://www.smartjava.org/content/building-rest-service-scala-akka-http-akka-streams-and-reactive-mongo
