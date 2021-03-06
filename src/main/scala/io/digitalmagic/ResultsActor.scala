package io.digitalmagic

import akka.actor.Actor
import io.digitalmagic.ResultsActor._

import scala.util.Random

object ResultsActor {
  // Data structures
  //case class Email(value: String) extends AnyVal
  type Email = String
  case class Result(rate: Int, count: Int)

  // Inbound messages
  case class Submit(email: Email, rate: Int)
  case object GetResults
  case object GetWinner

  // Outbound messages
  case object Ack
  case class Results(results: Set[Result])
  case class Winner(email: Email)
  case class Error(msg: String)
}

class ResultsActor extends Actor {

  private var state = Map.empty[Email, Submit]
  private var rand = new Random(System.nanoTime)

  def receive: Actor.Receive = {
    case s: Submit =>
      state += s.email -> s
      sender() ! Ack
    case GetResults =>
      val result = state.values.foldLeft(Map.empty[Int, Int]) { (res, e) =>
        if (res.contains(e.rate)) {
          val v: Int = res(e.rate) + 1
          res + (e.rate -> v)
        } else {
          res + (e.rate -> 1)
        }
      }
      sender() ! Results(result.map(t => Result(t._1, t._2)).toSet)
    case GetWinner =>
      if (state.size < 1) {
        sender() ! Error("Nobody to Win")
      }
      val rnd = rand.nextInt(state.size)
      sender() ! Winner(state.keys.iterator.drop(rnd).next())
  }

}
