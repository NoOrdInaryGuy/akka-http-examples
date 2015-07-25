package io.neilord

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FlowGraph.Implicits._
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.ExecutionContext.Implicits.global

object Client extends App {
  implicit val system = ActorSystem("testsystem")
  implicit val materializer = ActorMaterializer()

  val httpClient = Http(system).outgoingConnection("localhost", 8091)

  //Define a sink to process the response
  val printChunksConsumer = Sink.foreach[HttpResponse] { res =>
    if(res.status == StatusCodes.OK) {
      println(s"Response received: $res")

      res.entity.dataBytes.map {
        chunk: ByteString =>
          val chunkString = chunk.decodeString(HttpCharsets.`UTF-8`.value)
          println(s"Chunk: $chunkString")
      }.to(Sink.ignore).run()

    } else {
      println(res.status)
    }
  }


  val requestDoublerFlow = Sink() {
    implicit builder =>
      val bcast = builder.add(Broadcast[HttpRequest](2))
      val concat = builder.add(Concat[HttpResponse]())
      val printChunks = builder.add(printChunksConsumer)

      //Test graph == Duplicate and send twice, concat result
                bcast ~> httpClient ~> concat
                bcast ~> httpClient ~> concat ~> printChunks
      bcast.in
  }

  //5 requests for ticker GOOG, through requestDoublerFlow which doubles and concats them
  val res = 1 to 5 foreach { _ =>
    Source.single(HttpRequest(uri = Uri("/get?ticker=GOOG")))
      .to(requestDoublerFlow)
      .run()
  }

  // Make some calls with filled in request URI
  //  via = transform the Flow by appending the given processing stages
  //  runWith == connect the source to the provided sink
  val fAll = Source.single(HttpRequest(uri = Uri("/getAllTickers"))).via(httpClient).runWith(printChunksConsumer)
  val fTicker = Source.single(HttpRequest(uri = Uri("/get?ticker=ADAT"))).via(httpClient).runWith(printChunksConsumer)
  val fTypo = Source.single(HttpRequest(uri = Uri("/get?oops=FNB"))).via(httpClient).runWith(printChunksConsumer)

  for {
    f2Result <- fAll
    f2Result <- fTicker
    f2Result <- fTypo
  } yield {
    println("All calls done")
    system.shutdown()
    system.awaitTermination()
  }

}
