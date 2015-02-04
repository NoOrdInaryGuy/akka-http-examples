package io.neilord

import reactivemongo.bson.BSONDocument
import play.api.libs.json.Json
import play.modules.reactivemongo.json.BSONFormats

trait BSONUtils {
  //use the play json libraries to convert BSON to json
  def convertToString(input: List[BSONDocument]) : String = {
    input
      .map(f => convertToString(f))
      .mkString("[", ",", "]")
  }

  def convertToString(input: BSONDocument) : String = {
    Json.stringify(BSONFormats.toJSON(input))
  }
}
