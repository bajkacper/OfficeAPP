import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.domain.infra.db.{OfficeDAO, ShipmentDAO}
import com.domain.infra.endpoints.{OfficeEndpoint, ShipmentEndpoint}
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import cats.implicits._
import scala.concurrent.ExecutionContext.global

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    AppConfig.load().flatMap {
      case (serverConfig, dbConfig) =>
        createTransactor(dbConfig).use { transactor =>
          val combinedRoutes: HttpRoutes[IO]=OfficeEndpoint(OfficeDAO(transactor)).service.combineK(ShipmentEndpoint(ShipmentDAO(transactor)).service)
          EmberServerBuilder.default[IO]
            .withHost(serverConfig.host)
            .withPort(serverConfig.port)
            .withHttpApp(combinedRoutes.orNotFound)
            .build
            .use(_ => IO.never) // Keeps the server running indefinitely
            .as(ExitCode.Success)
        }
    }

  private def createTransactor(dbConfig: DatabaseConfig): Resource[IO, HikariTransactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      driverClassName = dbConfig.driver,
      url = dbConfig.url,
      user = dbConfig.user,
      pass = dbConfig.password,
      connectEC = global
    )
}
