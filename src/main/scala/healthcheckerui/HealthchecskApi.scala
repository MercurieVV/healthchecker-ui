package healthcheckerui

import cats.effect.IO
import hammock._
import hammock.circe.implicits._
import hammock.js.Interpreter
import hammock.marshalling._
import io.circe.generic.auto._
import model._

/**
 * Created with IntelliJ IDEA.
 * User: Victor Mercurievv
 * Date: 11/11/2019
 * Time: 5:31 AM
 * Contacts: email: mercurievvss@gmail.com Skype: 'grobokopytoff' or 'mercurievv'
 */
class HealthchecskApi {
  private implicit val interpreter = Interpreter[IO]
//  implicit def wsIdFromString(i: String): WsIdEnum = WsIdEnum.withName(i)
//  implicit def resultFromString(i: String): ResultEnum = ResultEnum.withName(i)
//  implicit val checkIdV: Validate[String, CheckIdTag] = Validate.alwaysPassed("")
//  implicit val checkIdD: io.circe.Decoder[CheckId] = refinedDecoder[String, CheckIdTag, @@]
implicit val decodeInstant: io.circe.Decoder[CheckId] = io.circe.Decoder.decodeString.emap { str =>
  Right(CheckId(str))
}
  val healthchecks: IO[List[Healthcheck]] = Hammock
    .request(Method.GET, uri"https://healthchecker-ws.internal.next-wireless.co/api/v1/healthchecks", Map()) // In the `request` method, you describe your HTTP request
    .as[List[Healthcheck]]
    .exec[IO]

}
