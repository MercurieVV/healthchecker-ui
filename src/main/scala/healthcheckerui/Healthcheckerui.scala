package healthcheckerui

import outwatch.dom._
import outwatch.dom.dsl._

import monix.reactive.Observable
import monix.execution.Scheduler.Implicits.global

import concurrent.duration._

object Main {
  def main(args: Array[String]): Unit = {

    val counter = Observable.interval(1 second)
    val counterComponent = div("count: ", counter)

    OutWatch.renderReplace("#app", counterComponent).unsafeRunSync()
  }
}
