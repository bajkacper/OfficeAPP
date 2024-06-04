package com.domain.infra.endpoints

import cats.effect.IO
import com.domain.Errors._
import com.domain.infra.db.ShipmentDAO
import com.domain.shipments.{Shipment, ShipmentType}
import io.circe.syntax._
import io.circe.generic.semiauto._
import io.circe.{Encoder, Decoder}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.EntityDecoder
import org.http4s.{HttpRoutes, Response}

case class ShipmentEndpoint(dao: ShipmentDAO) extends Http4sDsl[IO] {
  implicit val decodeShipmentType: Decoder[ShipmentType] = Decoder.decodeString.map[ShipmentType](ShipmentType.unsafeFromString)
  implicit val encodeShipmentType: Encoder[ShipmentType] = Encoder.encodeString.contramap[ShipmentType](_.value)
  implicit val shipmentDecoder: Decoder[Shipment] = deriveDecoder[Shipment]
  implicit val shipmentEncoder: Encoder[Shipment] = deriveEncoder[Shipment]
  implicit val listShipmentEncoder: Encoder[List[Shipment]] = Encoder.encodeList[Shipment]

  private implicit val shipmentEntityDecoder: EntityDecoder[IO, Shipment] = jsonOf[IO, Shipment]

  val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "shipments" =>
      for {
        shipments <- dao.list()
        response <- Ok(shipments.asJson)
      } yield response

    case req @ POST -> Root / "shipments" =>
      req.as[Shipment].flatMap { shipment =>
        for {
          result <- dao.create(shipment)
          response <- handleResult(result)
        } yield response
      }

    case req @ PUT -> Root / "shipments" =>
      req.as[Shipment].flatMap { shipment =>
        for {
          result <- dao.update(shipment)
          response <- handleResult(result)
        } yield response
      }

    case DELETE -> Root / "shipments" / LongVar(id) =>
      dao.delete(id).flatMap {
        case Right(_) => NoContent()
        case Left(error) => handleResult(Left(error))
      }
  }

  private def handleResult(result: Either[Errors, Shipment]): IO[Response[IO]] = {
    result match {
      case Right(item) => Ok(item.asJson)
      case Left(NotFoundError) => NotFound(NotFoundError.message)
      case Left(UniqueConstraintError) => InternalServerError(UniqueConstraintError.message)
      case Left(customError: CustomError) => InternalServerError(customError.message)
      case Left(otherError) => InternalServerError(otherError.toString)
    }
  }
}
