package io.neilord

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

object BootWithRouting extends App {
  val host = "127.0.0.1"
  val port = 8093

  implicit val system = ActorSystem("my-testing-system")
  implicit val fm = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
    Http(system).bind(interface = host, port = port)

  val route: Route =
    path("") {
      get {
        complete(HttpResponse(entity = "Hello world?"))
      }
    }

  serverSource.to(Sink.foreach {
    connection =>
      println("Accepted new connection from: " + connection.remoteAddress)
      connection handleWith Route.handlerFlow(route)
  }).run()

}
