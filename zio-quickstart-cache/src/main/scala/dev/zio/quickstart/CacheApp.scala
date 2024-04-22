package dev.zio.quickstart

import zio._
import zio.cache.{Cache, Lookup}
import java.time.temporal.ChronoUnit

object CacheApp extends ZIOAppDefault {
  private val key = "key1"

  private def timeConsumingEffect(key: String) =
    ZIO.sleep(5.seconds).as(key.hashCode)

  def run =
    for {
      cache <- Cache.make(
        capacity = 100,
        timeToLive = Duration.Infinity,
        lookup = Lookup(timeConsumingEffect)
      )
      _         <- Console.printLine(s"Start getting results by $key:")
      startTime <- Clock.currentTime(ChronoUnit.SECONDS)
      result <- cache
        .get(key)
        .zipPar(cache.get(key))
        .zipPar(cache.get(key))
      endTime <- Clock.currentTime(ChronoUnit.SECONDS)
      _ <- ZIO.debug(
        s"Result of parallel execution of three effects with $key: $result"
      )

      hits                 <- cache.cacheStats.map(_.hits)
      misses               <- cache.cacheStats.map(_.misses)
      keyLastLoadedTimeOpt <- cache.entryStats(key)
      keyLastLoadedTime    <- ZIO.fromOption(keyLastLoadedTimeOpt).map(_.loaded)
      _                    <- ZIO.debug(s"Number of cache hits: $hits")
      _                    <- ZIO.debug(s"Number of cache misses: $misses")
      _ <- ZIO.debug(s"The last time of getting $key: $keyLastLoadedTime")
      _ <- ZIO.debug(
        s"Time to get result by $key 3 times: ${endTime - startTime} seconds"
      )
    } yield ()

}
