import cats.effect.IO
import com.typesafe.config.ConfigFactory
import com.comcast.ip4s.{Host, Port}

case class DatabaseConfig(driver: String, url: String, user: String, password: String)
case class ServerConfig(host: Host, port: Port)

object AppConfig {
  def load(configFile: String = "application.conf"): IO[(ServerConfig, DatabaseConfig)] = IO {
    val config = ConfigFactory.load(configFile)
    val serverHost = Host.fromString(config.getString("server.host")).getOrElse(throw new IllegalArgumentException("Invalid server host"))
    val serverPort = Port.fromInt(config.getInt("server.port")).getOrElse(throw new IllegalArgumentException("Invalid server port"))

    val serverConfig = ServerConfig(host = serverHost, port = serverPort)

    val databaseConfig = DatabaseConfig(
      driver = config.getString("database.driver"),
      url = config.getString("database.url"),
      user = config.getString("database.user"),
      password = config.getString("database.password")
    )
    (serverConfig, databaseConfig)
  }
}
