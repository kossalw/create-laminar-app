package web

import com.raquo.laminar.api.L.*

import org.scalajs.dom
import scala.scalajs.js
import js.annotation.*

import controller.State.*
import controller.Action.*
import FromTo.*

object Main {
  def ratesSplitter(currentValue: Signal[String]) =
    children <-- ratesSignal.map(_.toSeq).split(_._1) { case (_, _, rateSignal) =>
      val currencySignal = rateSignal.map(_._1)
      option(
        value <-- currencySignal,
        selected <-- currentValue.combineWith(currencySignal).map(_ == _),
        child.text <-- currencySignal
      )
    }

  val app =
    div(
      label(
        forId := "amount",
        "Input an amount: "
      ),
      input(
        idAttr := "amount",
        nameAttr := "amount",
        typ := "number",
        onInput.mapToValue.map(value => ChangeAmount(value.toDouble)) --> actionStream.writer,
        value <-- amountSignal.map(_.toString)
      ),
      label(
        forId := "from-currency",
        "Currency of amount: "
      ),
      select(
        idAttr := "from-currency",
        nameAttr := "from-currency",
        value <-- fromCurrencySignal,
        ratesSplitter(fromCurrencySignal),
        onChange.mapToValue.map(value => SelectCurrency(value, From)) --> actionStream.writer
      ),
      label(
        forId := "to-currency",
        "Target to convert: "
      ),
      select(
        idAttr := "to-currency",
        nameAttr := "to-currency",
        value <-- toCurrencySignal,
        ratesSplitter(toCurrencySignal),
        onChange.mapToValue.map(value => SelectCurrency(value, To)) --> actionStream.writer
      ),
      button(
        onClick.preventDefault.mapTo(ConvertAmount) --> actionStream.writer,
        "Convert amount"
      ),
      p(
        "Converted amount: ",
        child.text <-- convertedAmountSignal.map(_.fold("")(_.toString))
      )
    )

  @JSExportTopLevel("main")
  def main(): Unit = {
    renderOnDomContentLoaded(dom.document.getElementById("root"), app)
  }
}
