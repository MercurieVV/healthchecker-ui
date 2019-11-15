package healthcheckerui

import cats.effect.IO
import model.{Check, Healthcheck}
import outwatch.dom._
import outwatch.dom.dsl._
import monix.reactive.Observable
import outwatch.http.Http
import outwatch.http.Http.{BodyType, Request}
import monix.execution.Scheduler.Implicits.global
import enumeratum._
import hammock.HammockF
import hammock.js.Interpreter
import healthcheckerui.Main.<
import model.Check.ResultEnum
import model.Check.ResultEnum.{fail, success}
import monix.execution.Ack.Continue
import outwatch.dom.{Handler, OutWatch, VNode, dsl}
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import org.scalajs.dom.Element
import outwatch.ObserverBuilder
import shapeless.tag._
import shapeless._

import concurrent.duration._

object Main {

  def main(args: Array[String]): Unit = {

    val api                             = new HealthchecskApi()
    val response: IO[List[Healthcheck]] = api.healthchecks
    val its = {
      for {
        itemId <- Handler.create[Healthcheck]
        resp   <- response.map(_.groupBy(_.ws_id).mapValues(_.flatMap(_.checks)).map(kv => Healthcheck(kv._1, kv._2)).toList).map(Observable.pure)
        result <- OutWatch.renderReplace("#app", main(resp, itemId))
      } yield result
    }
    its.unsafeRunAsync(e => println(e))
  }

  val < = dsl.tags
  val ^ = dsl.attributes

  private def main(menuItems: Observable[List[Healthcheck]], selectedItem: Handler[Healthcheck]) = {
    val updateEffectSink = ObserverBuilder.create[(Element, Element)] {
      case (_, _) =>
        scala.scalajs.js.eval("onRendered();")
        Continue
    }
    <.div(
        ^.className := "full height",
        listItems(menuItems, selectedItem),
        <.div(^.className := "ui container")(
            <.form(
                ^.className := "ui form",
                <.div(^.className := "ui header", selectedItem.map(_.ws_id.entryName)),
                <.div(
                    ^.className := "ui fluid styled accordion",
                    selectedItem.map(
                        _.checks
                          .sortBy(c => ResultEnum.indexOf(c.result))
                          .reverse
                          .map(
                              check =>
                                List(
                                    <.div(
                                        ^.className := "title",
                                        <.i(^.className := s"circle ${getItemColor(List(check))} icon"),
                                        <.label(^.className := "ui header", check.id),
                                        <.div(
                                          <.i(^.className := s"dropdown icon"),
                                          <.label(check.description)
                                        ),
                                    ),
                                    <.div(^.className := "content", <.p(check.fail_message))
                                )
                          )
                    ),
                    onSnabbdomUpdate --> updateEffectSink
                )
            )
        )
    )

  }

  private def getItemColor(checks: List[Check]) = if (checks.exists(_.result == fail)) "red" else "green"

  private def listItems(menuItems: Observable[List[Healthcheck]], selectedItemId: Handler[Healthcheck]) = {
    <.div(
        ^.className := "toc",
        <.div(
            ^.className := "ui sidebar inverted vertical menu visible fixed",
            menuItems.map(
                _.map(
                    shopHc =>
                      <.div(
                          ^.className := "item ",
                          <.div(^.className := "header", shopHc.ws_id.entryName),
                          <.div(
                              ^.className := "menu",
                              <.div(
                                  ^.className := "item visible",
                                  <.div(s"${shopHc.checks.size}/${shopHc.checks.count(_.result == success)}"),
                                  <.i(^.className := s"circle ${getItemColor(shopHc.checks)} icon")
                              )
                          ),
                          ^.onClick(shopHc) --> selectedItemId
                      )
                )
            )
        )
    )
  }

}
