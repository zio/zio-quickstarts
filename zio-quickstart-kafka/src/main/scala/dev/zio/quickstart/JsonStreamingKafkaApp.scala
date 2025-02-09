package dev.zio.quickstart

import org.apache.kafka.clients.producer.ProducerRecord
import zio._
import zio.json._
import zio.kafka.consumer._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde._
import zio.stream.ZStream

import java.time.OffsetDateTime
import java.util.UUID

/** This is the data we will be sending to Kafka in JSON format. */
case class Event(uuid: UUID, timestamp: OffsetDateTime, message: String)

/** A zio-json encoder/decoder for [[Event]]. */
object Event {
  implicit val encoder: JsonEncoder[Event] =
    DeriveJsonEncoder.gen[Event]

  implicit val decoder: JsonDecoder[Event] =
    DeriveJsonDecoder.gen[Event]
}

/** A zio-kafka serializer/deserializer for [[Event]]. */
object EventKafkaSerde {
  val event: Serde[Any, Event] =
    Serde.string.inmapZIO[Any, Event](s =>
      ZIO
        .fromEither(s.fromJson[Event])
        .mapError(e => new RuntimeException(e))
    )(r => ZIO.succeed(r.toJson))
}

object JsonStreamingKafkaApp extends ZIOAppDefault {
  private val BOOSTRAP_SERVERS = List("localhost:9092")
  private val KAFKA_TOPIC      = "json-streaming-hello"

  def run: ZIO[Any, Throwable, Unit] = {
    val p: ZIO[Any, Throwable, Unit] =
      ZIO.scoped {
        for {
          producer <- Producer.make(ProducerSettings(BOOSTRAP_SERVERS))
          _ <- ZStream
            .repeatZIO(Random.nextUUID <*> Clock.currentDateTime)
            .schedule(Schedule.spaced(1.second))
            .map { case (uuid, time) =>
              new ProducerRecord(
                KAFKA_TOPIC,
                time.getMinute,
                Event(uuid, time, "Hello, World!")
              )
            }
            .via(producer.produceAll(Serde.int, EventKafkaSerde.event))
            .runDrain
        } yield ()
      }

    val c: ZIO[Any, Throwable, Unit] =
      ZIO.scoped {
        for {
          consumer <- Consumer.make(
            ConsumerSettings(BOOSTRAP_SERVERS).withGroupId(
              "streaming-kafka-app"
            )
          )
          _ <- consumer
            .plainStream(
              Subscription.topics(KAFKA_TOPIC),
              Serde.int,
              EventKafkaSerde.event
            )
            .tap { r =>
              val event: Event = r.value
              Console.printLine(
                s"Event ${event.uuid} was sent at ${event.timestamp} with message ${event.message}"
              )
            }
            .map(_.offset)
            .aggregateAsync(Consumer.offsetBatches)
            .mapZIO(_.commit)
            .runDrain
        } yield ()
      }

    p <&> c
  }

}
