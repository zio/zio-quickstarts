package dev.zio.quickstart

import org.apache.kafka.clients.producer.RecordMetadata
import zio._
import zio.kafka.consumer._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde._

/** A simple app that produces and consumes messages from a kafka cluster
  * without using ZIO Streams.
  */
object SimpleKafkaApp extends ZIOAppDefault {
  private val BOOSTRAP_SERVERS = List("localhost:29092")
  private val KAFKA_TOPIC = "hello"

  private def produce(
      topic: String,
      key: Long,
      value: String
  ): RIO[Any with Producer, RecordMetadata] =
    Producer.produce[Any, Long, String](
      topic = topic,
      key = key,
      value = value,
      keySerializer = Serde.long,
      valueSerializer = Serde.string
    )

  private def consumeAndPrintEvents(
      groupId: String,
      topic: String
  ): RIO[Any, Unit] =
    Consumer.consumeWith(
      settings = ConsumerSettings(BOOSTRAP_SERVERS)
        .withGroupId(groupId),
      subscription = Subscription.topics(topic),
      keyDeserializer = Serde.long,
      valueDeserializer = Serde.string
    )(record => Console.printLine((record.key(), record.value())).orDie)

  private val producer: ZLayer[Any, Throwable, Producer] =
    ZLayer.scoped(
      Producer.make(
        ProducerSettings(BOOSTRAP_SERVERS)
      )
    )

  def run =
    for {
      c <- consumeAndPrintEvents("simple-kafka-app", KAFKA_TOPIC).fork
      p <-
        Clock.currentDateTime
          .flatMap { time =>
            produce(KAFKA_TOPIC, time.getHour.toLong, s"$time -- Hello, World!")
          }
          .schedule(Schedule.spaced(1.second))
          .provide(producer)
          .fork
      _ <- (c <*> p).join
    } yield ()

}
