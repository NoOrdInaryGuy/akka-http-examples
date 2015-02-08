package io.neilord

import akka.actor.ActorSystem
import akka.stream.FlowMaterializer
import scala.concurrent.Future
import akka.http.model._
import akka.http._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.stream.scaladsl._
import scala.util.Random
import akka.http.model.HttpResponse
import akka.stream.scaladsl.FlowGraphImplicits._

object BootWithFlow extends App with BSONUtils {

  implicit val system = ActorSystem("Streams")
  implicit val materializer = FlowMaterializer()

  //Start the server
  val serverBinding = Http().bind(interface = "localhost", port = 8090)
  serverBinding.connections.foreach { connection =>
    println(s"Connection accepted from ${connection.remoteAddress}")
//    connection handleWith { broadCastZipFlow }
    connection handleWith { broadCastMergeFlow }
  }

  //Some dummy steps that totally disregard the request, and return an entry of their choosing.
  val step1 = Flow[HttpRequest].mapAsync[String](getTickerHandler("GOOG"))
  val step2 = Flow[HttpRequest].mapAsync[String](getTickerHandler("AAPL"))
  val step3 = Flow[HttpRequest].mapAsync[String](getTickerHandler("MSFT"))

  val mapStringToResponse = Flow[String].map[HttpResponse](
    (in: String) => HttpResponse(status = StatusCodes.OK, entity = in)
  )

  //These will be plumbed into the source and sink of the HTTP connection, we leave them
  //undefined here.
  val in = UndefinedSource[HttpRequest]
  val out = UndefinedSink[HttpResponse]

  //"To Many"
  val bCast = Broadcast[HttpRequest]

  //"From Many" - Merge takes the first available response
  val merge = Merge[String]

  //"From Many" - ZipWith combines the inputs (3 here) into some result (HttpResponse). Set them using .inputX.
  val zip = ZipWith[String, String, String, HttpResponse] {
    (inp1, inp2, inp3) => new HttpResponse(status = StatusCodes.OK,entity = inp1 + inp2 + inp3)
  }

  //There's a faked delay in step1-3, so you don't know which one you will get, it's realistic.
  val broadCastMergeFlow = Flow[HttpRequest, HttpResponse]() {
    implicit builder =>
      //Input -> Splitter -> [ Actions ] -> Merge (take first policy) -> Create an HttpResponse -> Output
            bCast ~> step1 ~> merge
      in ~> bCast ~> step2 ~> merge ~> mapStringToResponse ~> out
            bCast ~> step3 ~> merge
      (in, out)
  }

  val broadCastZipFlow = Flow[HttpRequest, HttpResponse]() {
    implicit builder =>
      //Input -> Splitter -> [ Actions ] -> Zip (combine all policy, ours makes an HttpResponse) -> Output
            bCast ~> step1 ~> zip.input1
      in ~> bCast ~> step2 ~> zip.input2 ~> out
            bCast ~> step3 ~> zip.input3
      (in, out)
  }

  def getTickerHandler(tickName: String)(request: HttpRequest): Future[String] = {
    // query the database
    val tickerFO = Database.findTicker(tickName)
    Thread.sleep(Random.nextInt(10) * 1000)

    tickerFO map {
      _ map convertToString getOrElse ""
    }
  }


}
