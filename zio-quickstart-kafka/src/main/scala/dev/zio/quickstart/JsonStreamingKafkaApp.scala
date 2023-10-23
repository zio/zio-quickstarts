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

case class Event(uuid: UUID, timestamp: OffsetDateTime, message: String)

object Event {
  implicit val encoder: JsonEncoder[Event] =
    DeriveJsonEncoder.gen[Event]

  implicit val decoder: JsonDecoder[Event] =
    DeriveJsonDecoder.gen[Event]
}

object KafkaSerde {
  val key: Serde[Any, Int] =
    Serde.int

  val value: Serde[Any, Event] =
    Serde.string.inmapM[Any, Event](s =>
      ZIO
        .fromEither(s.fromJson[Event])
        .mapError(e => new RuntimeException(e))
    )(r => ZIO.succeed(r.toJson))
}

object JsonStreamingKafkaApp extends ZIOAppDefault {
  private val BOOSTRAP_SERVERS = List("localhost:29092")
  private val KAFKA_TOPIC      = "json-streaming-hello"

  private val producer: ZLayer[Any, Throwable, Producer] =
    ZLayer.scoped(
      Producer.make(
        ProducerSettings(BOOSTRAP_SERVERS)
      )
    )

  private val consumer: ZLayer[Any, Throwable, Consumer] =
    ZLayer.scoped(
      Consumer.make(
        ConsumerSettings(BOOSTRAP_SERVERS)
          .withGroupId("streaming-kafka-app")
      )
    )

  def run = {
    val p: ZStream[Producer, Throwable, Nothing] =
      ZStream
        .repeatZIO(Random.nextUUID <*> Clock.currentDateTime)
        .schedule(Schedule.spaced(1.second))
        .map { case (uuid, time) =>
          new ProducerRecord(
            KAFKA_TOPIC,
            time.getMinute,
            Event(uuid, time, "Hello, World!")
          )
        }
        .via(Producer.produceAll(KafkaSerde.key, KafkaSerde.value))
        .drain

    val c: ZStream[Consumer, Throwable, Nothing] =
      Consumer
        .plainStream(
          Subscription.topics(KAFKA_TOPIC),
          KafkaSerde.key,
          KafkaSerde.value
        )
        .tap(e => Console.printLine(e.value))
        .map(_.offset)
        .aggregateAsync(Consumer.offsetBatches)
        .mapZIO(_.commit)
        .drain

    (p merge c).runDrain.provide(producer, consumer)
  }

}
