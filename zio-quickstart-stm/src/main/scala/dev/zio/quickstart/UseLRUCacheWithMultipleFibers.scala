package dev.zio.quickstart

import zio._

// thanks jorge-vasquez-2301 for this example
object UseLRUCacheWithMultipleFibers extends ZIOAppDefault {
  lazy val run =
    (for {
      fiberReporter  <- reporter.forever.fork
      fiberProducers <- startWorkers(producer)
      fiberConsumers <- startWorkers(consumer)
      _ <-
        Console.readLine.orDie *> (fiberReporter <*> fiberProducers <*> fiberConsumers).interrupt
    } yield ()).provideLayer(layer)

  lazy val layer = LRUCacheSTM.layer[Int, Int](capacity = 3)

  def startWorkers(worker: URIO[LRUCache[Int, Int], Unit]) =
    ZIO.forkAll {
      ZIO.replicate(100) {
        worker.forever.catchAllCause(cause =>
          Console.printLineError(cause.prettyPrint)
        )
      }
    }

  lazy val producer: URIO[LRUCache[Int, Int], Unit] =
    for {
      number <- Random.nextIntBounded(100)
      _ <- Console.printLine(s"Producing ($number, $number)").orDie *> LRUCache
        .put(number, number)
    } yield ()

  lazy val consumer: URIO[LRUCache[Int, Int], Unit] =
    (for {
      key <- Random.nextIntBounded(100)
      value <- Console
        .printLine(s"Consuming key: $key") *> LRUCache.get[Int, Int](key)
      _ <- Console.printLine(s"Consumed value: $value")
    } yield ()).catchAll(ex => Console.printLine(ex.getMessage).orDie)

  lazy val reporter: URIO[LRUCache[Int, Int], Unit] =
    for {
      status <- LRUCache.getStatus[Int, Int]
      (items, optionStart, optionEnd) = status
      _ <- Console
        .printLine(s"Items: $items, Start: $optionStart, End: $optionEnd")
        .orDie
    } yield ()
}
