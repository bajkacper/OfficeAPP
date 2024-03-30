package com.domain.infra.endpoints

import cats.effect.IO
import com.domain.Errors._
import com.domain.infra.db.OfficeDAO
import com.domain.offices.Office
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import io.circe.syntax._

case class OfficeEndpoint(dao: OfficeDAO) extends Http4sDsl[IO] {

  implicit val decodeOffice: Decoder[Office] = deriveDecoder[Office]
  implicit val encodeOffice: Encoder[Office] = deriveEncoder[Office]

  val service: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root / "offices" =>
      for {
        getResult <- dao.list()
        response <- Ok(getResult.asJson)
      } yield response

    case req @ POST -> Root / "offices" =>
      for {
        office <- req.decodeJson[Office]
        updateResult <- dao.create(office)
        response <- result(updateResult)
      } yield response

    case GET -> Root / "offices" / LongVar(id) =>
      for {
        getResult <- dao.get(id)
        response <- result(getResult)
      } yield response

    case req @ PUT -> Root / "offices" =>
      for {
        office <- req.decodeJson[Office]
        updateResult <- dao.update(office)
        response <- result(updateResult)
      } yield response

    case DELETE -> Root / "offices" / LongVar(id) =>
      dao.delete(id).flatMap {
        case Right(_) => NoContent()        case Left(NotFoundError) => NotFound(NotFoundError.message)
        case Left(c:Errors) => InternalServerError(c.message)
        case Left(e) => InternalServerError(e.toString)
      }
  }

  def result(result: Either[Errors, Office]) = {
    result match {
      case Left(NotFoundError) => NotFound(NotFoundError.message)
      case Left(UniqueConstraintError) => InternalServerError(UniqueConstraintError.message)
      case Left(c:CustomError) => InternalServerError(c.message)
      case Left(e) => InternalServerError(e.toString)
      case Right(item) => Ok(item.asJson)
    }
  }

}
