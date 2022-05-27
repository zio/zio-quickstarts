package dev.zio.quickstart.config

import zio._
import zio.config._
import zio.config.magnolia.descriptor
import zio.config.typesafe.TypesafeConfigSource

case class HttpServerConfig(host: String, port: Int)

object HttpServerConfig {
  val layer: ZLayer[Any, ReadError[String], HttpServerConfig] =
    ZLayer {
      read {
        descriptor[HttpServerConfig].from(
          TypesafeConfigSource.fromResourcePath
            .at(PropertyTreePath.$("HttpServerConfig"))
        )
      }
    }
}
