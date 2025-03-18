package dev.zio.quickstart

import org.apache.kafka.clients.producer.ProducerRecord
import zio._
import zio.kafka.consumer._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde._
import zio.stream.ZStream

object StreamingKafkaApp extends ZIOAppDefault {
  private val BOOSTRAP_SERVERS = List("localhost:9092")
  private val KAFKA_TOPIC      = "streaming-hello"

  private val producerSettings = ProducerSettings(BOOSTRAP_SERVERS)
  private val consumerSettings =
    ConsumerSettings(BOOSTRAP_SERVERS).withGroupId("streaming-kafka-app")

  def run: ZIO[Any, Throwable, Unit] = {
    val p: ZIO[Any, Throwable, Unit] =
      ZIO.scoped {
        for {
          producer <- Producer.make(producerSettings)
          _ <- ZStream
            .repeatZIO(Clock.currentDateTime)
            .schedule(Schedule.spaced(1.second))
            .map { time =>
              new ProducerRecord(
                KAFKA_TOPIC,
                time.getMinute,
                s"$time -- Hello, World!"
              )
            }
            .via(producer.produceAll(Serde.int, Serde.string))
            .runDrain
        } yield ()
      }

    val c: ZIO[Any, Throwable, Unit] =
      ZIO.scoped {
        for {
          consumer <- Consumer.make(consumerSettings)
          _ <- consumer
            .plainStream(
              Subscription.topics(KAFKA_TOPIC),
              Serde.int,
              Serde.string
            )
            // do not use `tap` in prod because it destroys the chunking structure and leads to lower performance
            // See https://zio.dev/zio-kafka/serialization-and-deserialization#a-warning-about-mapzio
            .tap(r => Console.printLine("Consumed: " + r.value))
            .map(_.offset)
            .aggregateAsync(Consumer.offsetBatches)
            .mapZIO(_.commit)
            .runDrain
        } yield ()
      }

    p <&> c
  }

}
