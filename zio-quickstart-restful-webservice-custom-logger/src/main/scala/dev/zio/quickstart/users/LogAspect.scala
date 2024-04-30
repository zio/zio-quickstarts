package dev.zio.quickstart.users

import zio._
import zio.http._
import zio.prelude.data.Optional.AllValuesAreNullable

object LogAspect {
  def logAnnotateCorrelationId: Middleware[Any] =
    new Middleware[Any] {
      override def apply[Env1 <: Any, Err](
          app: Routes[Env1, Err]
      ): Routes[Env1, Err] =
        app.transform { h =>
          handler { (req: Request) =>
            def correlationId(req: Request): UIO[String] =
              ZIO
                .succeed(req.headers.get("X-Correlation-ID"))
                .flatMap(x =>
                  Random.nextUUID.map(uuid => x.getOrElse(uuid.toString))
                )

            correlationId(req).flatMap(id =>
              ZIO.logAnnotate("correlation-id", id)(h(req))
            )
          }
        }
    }

  def logSpan(label: String): HandlerAspect[Any, Unit] =
    HandlerAspect.interceptIncomingHandler {
      Handler.fromFunctionZIO { (req: Request) =>
        ZIO.logSpan(label)(ZIO.succeed(req).map(r => (r, ())))
      }
    }

}
