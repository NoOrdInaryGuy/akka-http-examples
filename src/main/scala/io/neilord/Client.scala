package io.neilord

import akka.actor.ActorSystem
import akka.http.Http
import akka.http.model._
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.FlowGraphImplicits._
import akka.stream.scaladsl._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object Client extends App {
  implicit val system = ActorSystem("testsystem")
  implicit val materializer = FlowMaterializer()

  val httpClient1 = Http(system).outgoingConnection("localhost", 8090).flow
  val httpClient2 = Http(system).outgoingConnection("localhost", 8091).flow

  //Define a sink to process the response
  //TODO could also be a flow - exercise?
  val printChunksConsumer = Sink.foreach[HttpResponse] { res =>
    if(res.status == StatusCodes.OK) {
      println(s"Response received: $res")
      res.entity.getDataBytes().map {
        chunk =>
          val chunkString = chunk.decodeString(HttpCharsets.`UTF-8`.value)
                                 .substring(0, 80)
          println(s"Chunk: $chunkString")
      }.to(Sink.ignore).run()
    } else {
      println(res.status)
    }
  }

  val exampleRequestFlow: Sink[HttpRequest] = Sink[HttpRequest]() {
    implicit flowGraphBuilder =>
      // ???
      flowGraphBuilder.allowCycles()
      val source = UndefinedSource[HttpRequest]
      val bcast = Broadcast[HttpRequest]
      val concat = Concat[HttpResponse]

      //Test graph == Duplicate and send twice, concat result
                bcast ~> httpClient1 ~> concat.first
      source ~> bcast ~> httpClient1 ~> concat.second ~> printChunksConsumer
      source
  }

  val res = 1 to 5 map { _ =>
    Source.single(HttpRequest())
      .to(exampleRequestFlow)
      .run()
      .get(printChunksConsumer)
  }
  val f5RequestsSequenced = Future.sequence(res)

  // Make some calls with filled in request URI
  //  via = transform the Flow by appending the given processing stages
  //  runWith == connect the source to the provided sink
  val fAll = Source.single(HttpRequest(uri = Uri("/getAllTickers"))).via(httpClient2).runWith(printChunksConsumer)
  val fTicker = Source.single(HttpRequest(uri = Uri("/get?ticker=ADAT"))).via(httpClient2).runWith(printChunksConsumer)
  val fTypo = Source.single(HttpRequest(uri = Uri("/get?oops=FNB"))).via(httpClient2).runWith(printChunksConsumer)

  for {
    f2Result <- f5RequestsSequenced
    f2Result <- fAll
    f2Result <- fTicker
    f2Result <- fTypo
  } yield {
    println("All calls done")
    system.shutdown()
    system.awaitTermination()
  }

}
