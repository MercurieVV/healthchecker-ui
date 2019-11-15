package healthcheckerui

import cats.effect.IO
import model.Healthcheck
import outwatch.dom._
import outwatch.dom.dsl._
import monix.reactive.Observable
import outwatch.http.Http
import outwatch.http.Http.{BodyType, Request}
import monix.execution.Scheduler.Implicits.global
import enumeratum._
import hammock.HammockF
import hammock.js.Interpreter
import outwatch.dom.{Handler, OutWatch, VNode, dsl}
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import shapeless.tag._
import shapeless._

import concurrent.duration._

object Main {

  def main(args: Array[String]): Unit = {

    val api                             = new HealthchecskApi()
    val response: IO[List[Healthcheck]] = api.healthchecks
    val its = {
      for {
        itemId           <- Handler.create[Healthcheck.WsIdEnum]
        resp             <- response.map(Observable.pure)
//        counterComponent <- IO.pure(listItems(resp, itemId))
        result           <- OutWatch.renderReplace("#app", main(resp, itemId))
      } yield result
    }
    its.unsafeRunAsync(e => println(e))
  }

  val < = dsl.tags
  val ^ = dsl.attributes

  private def main(menuItems: Observable[List[Healthcheck]], selectedItemId: Handler[Healthcheck.WsIdEnum]) = {
    <.div(
        ^.className := "full height",
        listItems(menuItems, selectedItemId),
        <.div(^.className := "ui container")(
            <.form(
                ^.className := "ui form",
                <.div(^.className := "ui header", selectedItemId.map(_.entryName) /*, ^.hidden <-- selectedItemId*/ ),
                <.div(
                    ^.className := "ui header",
                    <.label("Page Id"), // <.input(^.onInput.value.transform(so => so.map(s => (tag[PageIdTag][String](s), s)).scan()) --> zzz),
                    <.label("Text Id")  // <.input(^.onInput.value.transform(transformCurrentTextId(newTextId, (i, o) => o.copy(_2 = i))) --> zzz),
//            ^.hidden <-- selectedItemId,
                )
//          editorForLang("eng", textFromRemote, onTextEdit, _.en, (t, s) => t.copy(en = s)),
            )
//        <.button(^.className := "ui teal large button", "Save", ^.onClick(combineTextAndItsIdToSave) --> saveText),
        )
    )

  }

  private def listItems(menuItems: Observable[List[Healthcheck]], selectedItemId: Handler[Healthcheck.WsIdEnum]) = {
    <.div(
        ^.className := "toc",
      <.div(
        ^.className := "ui sidebar inverted vertical menu visible",
        menuItems.map(
            _.map(
                shopHc =>
                      <.div(
                          ^.className := "item ",
                          <.div(^.className := "header", shopHc.ws_id.entryName),
                          <.div(
                              ^.className := "menu",
                              shopHc
                                .checks.flatMap(
                                    check =>
                                      List(
//                                    <.a(^.className := "item", ^.onClick(((shopHc.ws_id, check), false)) --> onClick, check),
                                          <.div(
                                              ^.className := "item visible",
                                              <.div(
                                                  check.id,
                                                  check.description,
                                                  check.result.entryName,
                                                  check.fail_message
                                              )
                                          )
//                  <.i(^.className := "minus icon tiny")
                                      )
                                )
                          )
                      )
                      /*          <.a(^.className := "item",
                      <.i(^.className := "plus inverted icon"),
                      ^.onClick(((tag[PageIdTag](""), ""), tg(true))) --> onClick,
                    ),*/
                  )
            )
        )
    )
  }

}
