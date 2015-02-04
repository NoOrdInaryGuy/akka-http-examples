package io.neilord

import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.MongoDriver
import scala.concurrent.Future
import reactivemongo.bson.BSONDocument
import collection.immutable.List
import scala.concurrent.ExecutionContext.Implicits.global

object Database {
  val collection = connect()

  def connect(): BSONCollection = {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost"))

    val db = connection("akka")
    db.collection("stocks")
  }

  def findAllTickers(): Future[List[BSONDocument]] = {
    val query = BSONDocument()
    val filter = BSONDocument(
      "Company" -> 1,
      "Country" -> 1,
      "Ticker" -> 1)

    Database.collection
      .find(query, filter)
      .cursor[BSONDocument]
      .collect[List]()
  }

  def findTicker(ticker: String): Future[Option[BSONDocument]] = {
    val query = BSONDocument("Ticker" -> ticker)

    Database.collection
      .find(query)
      .one
  }
}
