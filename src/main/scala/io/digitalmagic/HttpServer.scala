package io.digitalmagic

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route, StandardRoute}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class HttpServer(config: Config)(implicit executionContext: ExecutionContext) extends FailFastCirceSupport{

  val exceptionHandler = ExceptionHandler {
    case NonFatal(e) =>
      complete(HttpResponse(BadRequest, entity = e.getMessage()))
  }

  def completeString(response: String): StandardRoute =
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, response))

  val routes: Route = handleExceptions(exceptionHandler) {
    logRequestResult("DEMO") {
      pathPrefix("demo") {
        pathEndOrSingleSlash {
          get {
            completeString("TEST")
          }
        }
      }
    }
  }

}
