package io.neilord

import akka.actor.ActorSystem
import akka.stream.FlowMaterializer
import scala.concurrent.Future
import akka.http.model.HttpMethods._
import akka.http.model._
import akka.http._
import reactivemongo.bson.BSONDocument
import play.modules.reactivemongo.json.BSONFormats
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import akka.stream.scaladsl.{UndefinedSink, UndefinedSource, Broadcast, Flow}
import scala.util.Random

object BootWithFlow extends App with BSONUtils {

  implicit val system = ActorSystem("Streams")
  implicit val materializer = FlowMaterializer()

  //Start the server
  val serverBinding = Http().bind(interface = "localhost", port = 8090)
  serverBinding.connections.foreach { connection =>
    println(s"Connection accepted from ${connection.remoteAddress}")
    connection handleWith { ??? }
  }

  val bCast = Broadcast[HttpRequest]
  //Some dummy steps that totally disregard the request, and return an entry of their choosing.
  val step1 = Flow[HttpRequest].mapAsync[String](getTickerHandler("GOOG"))
  val step2 = Flow[HttpRequest].mapAsync[String](getTickerHandler("AAPL"))
  val step3 = Flow[HttpRequest].mapAsync[String](getTickerHandler("MSFT"))

  //These will be plumbed into the source and sink of the HTTP connection, we leave them
  //undefined here.
  val in = UndefinedSource[HttpRequest]
  val out = UndefinedSink[HttpResponse]

  //TODO: WIP


  def getTickerHandler(tickName: String)(request: HttpRequest): Future[String] = {
    // query the database
    val tickerFO = Database.findTicker(tickName)
    Thread.sleep(Random.nextInt(10) * 1000)

    tickerFO map {
      _ map convertToString getOrElse ""
    }
  }


}
