package dev.zio.quickstart

import zio._
import zio.kafka.consumer._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde._

/** A simple app that produces and consumes messages from a kafka cluster
  * without using ZIO Streams.
  */
object SimpleKafkaApp extends ZIOAppDefault {
  private val BOOSTRAP_SERVERS = List("localhost:9092")
  private val KAFKA_TOPIC      = "hello"

  def run: ZIO[Scope, Throwable, Unit] = {
    for {
      c <- Consumer
        .consumeWith(
          settings =
            ConsumerSettings(BOOSTRAP_SERVERS).withGroupId("simple-kafka-app"),
          subscription = Subscription.topics(KAFKA_TOPIC),
          keyDeserializer = Serde.long,
          valueDeserializer = Serde.string
        ) { record =>
          Console.printLine(s"Consumed ${record.key()}, ${record.value()}").orDie
        }
        .fork

      producer <- Producer.make(ProducerSettings(BOOSTRAP_SERVERS))
      p <- Clock.currentDateTime
        .flatMap { time =>
          producer.produce[Any, Long, String](
            topic = KAFKA_TOPIC,
            key = time.getHour.toLong,
            value = s"$time -- Hello, World!",
            keySerializer = Serde.long,
            valueSerializer = Serde.string
          )
        }
        .schedule(Schedule.spaced(1.second))
        .fork

      _ <- (c <*> p).join
    } yield ()
  }

}
