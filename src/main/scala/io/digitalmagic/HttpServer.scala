package io.digitalmagic

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route, StandardRoute}
import akka.util.Timeout
import io.digitalmagic.ResultsActor._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

import scala.util.{Failure, Success}
import scala.util.control.NonFatal
import scala.concurrent.duration._
import scala.language.postfixOps

class HttpServer(config: Config)(implicit system: ActorSystem) extends FailFastCirceSupport {

  private val actor = system.actorOf(Props[ResultsActor], name = "Results")
  private val metics: Metrics = new Metrics(config)

  private implicit val timeout: Timeout = Timeout(5 seconds)

  private val exceptionHandler = ExceptionHandler {
    case NonFatal(e) =>
      complete(HttpResponse(BadRequest, entity = e.getMessage()))
  }

  private def completeString(response: String): StandardRoute =
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, response))

  val routes: Route = handleExceptions(exceptionHandler) {
    import io.circe.generic.auto._
    import io.circe.syntax._

    logRequestResult("DEMO") {
      pathPrefix("demo") {
        get {
          pathEndOrSingleSlash {
            metics.createTick("request", Map("type" -> "form"))
            getFromResource("web/index.html")
          } ~ path("winner") {
            metics.createTick("request", Map("type" -> "winner"))
            onComplete(actor ? GetWinner) {
              case Success(Winner(email)) =>
                complete(email)
              case Success(Error(msg)) =>
                complete(msg)
              case Failure(f) =>
                complete(InternalServerError, s"Unable to read answers from DB: ${f.getMessage}")
              case _ =>
                complete(InternalServerError, s"Ooops! Something went wrong!")
            }
          } ~ path("css" / Segment) { filename =>
            metics.createTick("request", Map("type" -> "css"))
            getFromResource(s"web/css/$filename")
          } ~ path("images" / "favicon.ico") {
            metics.createTick("request", Map("type" -> "favicon"))
            getFromResource("web/images/favicon.ico", MediaTypes.`image/x-icon`)
          } ~ path("images" / Segment) { filename =>
            metics.createTick("request", Map("type" -> "images"))
            getFromResource(s"web/images/$filename", MediaTypes.`image/png`)
          } ~ path("js" / Segment) { filename =>
            metics.createTick("request", Map("type" -> "js"))
            getFromResource(s"web/js/$filename", ContentTypes.`text/plain(UTF-8)`)
          } ~ path("results") {
            metics.createTick("request", Map("type" -> "results"))
            onComplete(actor ? GetResults) {
              case Success(Results(answers)) =>
                complete(answers.asJson)
              case Failure(f) =>
                complete(InternalServerError, s"Unable to read answers from DB: ${f.getMessage}")
              case _ =>
                complete(InternalServerError, s"Ooops! Something went wrong!")
            }
          }
        } ~ post {
          metics.createTick("request", Map("type" -> "answer"))
          path("answer") {
            entity(as[Submit]) { submit =>
              validate(1 to 10 contains submit.rate, "Rate should be in range of 1..10") {
                onComplete(actor ? submit) {
                  case Success(resp) =>
                    redirect("results", StatusCodes.SeeOther)
                  case Failure(f) =>
                    complete(InternalServerError, s"Unable to append answer to DB: ${f.getMessage}")
                }
              }
            }
          }
        }
      }
    }
  }

}
