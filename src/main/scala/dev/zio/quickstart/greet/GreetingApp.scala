package dev.zio.quickstart.greet

import zhttp.http._

/**
 * An http app that: 
 *   - Accepts a `Request` and returns a `Response`
 *   - Does not fail
 *   - Does not use the environment
 */
object GreetingApp {

  //middlewares
  //trace id
  //auth -> user
  //auth -> impersonator
  //acl extractor
  //codec wrapper
  //config
  //client side http

  //  private val SECRET_KEY = ""

  // Helper to decode the JWT token
  //  def jwtDecode(token: String): Option[JwtClaim] = {
  //    Jwt.decode(token, SECRET_KEY, Seq(JwtAlgorithm.HS512)).toOption
  //  }

  def jwtDecode(token: String): Option[String] = {
    Some(token)
  }

  // Authentication middleware
  // Takes in a Failing HttpApp and a Succeed HttpApp which are called based on Authentication success or failure
  // For each request tries to read the `X-ACCESS-TOKEN` header
  // Validates JWT Claim
  def authenticate[R, E](success: String => HttpApp[R, E]): HttpApp[R, E] =
    Http
      .fromFunction[Request] { x =>
        x.headerValue("authorization")
          .flatMap(jwtDecode)
          .fold[HttpApp[R, E]](Http.forbidden("Not allowed!"))(success)
      }
      .flatten


  def apply(auth: String): UHttpApp =
    Http.collect[Request] {
      // GET /greet?name=:name
      case req@(Method.GET -> !! / "greet") if (req.url.queryParams.nonEmpty) =>
        Response.text(s"Hello $auth !")

      // GET /greet
      case Method.GET -> !! / "greet" =>
        Response.text(s"Hello $auth !")
      //        Response.text(s"Hello World!")

      // GET /greet/:name
      case Method.GET -> !! / "greet" / name =>
        Response.text(s"Hello $name!")
    }
}