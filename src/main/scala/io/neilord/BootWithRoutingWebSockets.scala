package io.neilord

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server._
import akka.http.scaladsl._
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.http.scaladsl.model.HttpResponse
import Directives._
import scala.concurrent.Future

object BootWithRoutingWebSockets extends App {
  val host = "127.0.0.1"
  val port = 8094

  implicit val system = ActorSystem("my-testing-system")
  implicit val fm = ActorFlowMaterializer()
  implicit val executionContext = system.dispatcher

  val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
    Http(system).bind(interface = host, port = port)

  val echoMessageFlow = Flow[Message].mapAsync[Message](1) {
    message => Future {
      println(s"Message received: " + message + ", echoing it back...")
      message
    }
  }

  val route: Route =
    path("") {
      get {
        //Handles WebSockets, falling back to normal complete if no upgrade.
        handleWebsocketMessages(echoMessageFlow) ~
        complete(HttpResponse(entity = "Hello non websockets world?"))
      }
    }

  serverSource.to(Sink.foreach {
    connection =>
      println("Accepted new connection from: " + connection.remoteAddress)
      connection handleWith Route.handlerFlow(route)
  }).run()

}
