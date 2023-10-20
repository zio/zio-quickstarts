package dev.zio.quickstart

import org.apache.kafka.clients.producer.ProducerRecord
import zio._
import zio.kafka.consumer._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde._
import zio.stream.ZStream

object StreamingKafkaApp extends ZIOAppDefault {
  private val BOOSTRAP_SERVERS = List("localhost:29092")
  private val KAFKA_TOPIC      = "streaming-hello"

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
        .repeatZIO(Clock.currentDateTime)
        .schedule(Schedule.spaced(1.second))
        .map(time => new ProducerRecord(KAFKA_TOPIC, time.getMinute, s"$time -- Hello, World!"))
        .via(Producer.produceAll(Serde.int, Serde.string))
        .drain
        
    val c: ZStream[Consumer, Throwable, Nothing] =
      Consumer
        .plainStream(Subscription.topics(KAFKA_TOPIC), Serde.int, Serde.string)
        .tap(e => Console.printLine(e.value))
        .map(_.offset)
        .aggregateAsync(Consumer.offsetBatches)
        .mapZIO(_.commit)
        .drain
    
    (p merge c).runDrain.provide(producer, consumer)
  }

}
