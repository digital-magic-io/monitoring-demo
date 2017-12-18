package io.digitalmagic

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.ExecutionContextExecutor

object Server extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: Materializer = ActorMaterializer()

  private val config = Config.load()
  private val restService = new HttpServer(config)
  Http().bindAndHandle(restService.routes, config.httpHost, config.httpPort)
}
