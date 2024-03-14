package web

import com.raquo.laminar.api.L.*

import org.scalajs.dom
import scala.scalajs.js
import js.annotation.*
import js.JSConverters.*

import typings.cashify
import cashify.mod.Cashify
import cashify.anon.{PartialOptions as CashifyOptions}

import org.scalablytyped.runtime.StringDictionary

object Web {
  @JSExportTopLevel("main")
  def main(args: Array[String]): Unit = {
    render(dom.document.getElementById("root"), app)
  }

  // Actions
  sealed trait Action
  case class ChangeAmount(amount: Double) extends Action
  case class SelectFromCurrency(currency: String) extends Action
  case class SelectToCurrency(currency: String) extends Action
  case object ConvertAmount extends Action

  val actionStream: EventBus[Action] = new EventBus[Action]

  // State
  case class State(
    rates: Map[String, Double],
    amount: Double,
    convertedAmount: Option[Double],
    fromCurrency: String,
    toCurrency: String
  )

  val freshState = State(
    rates = Map(
      "GBP" -> 0.92,
      "EUR" -> 1.00,
      "USD" -> 1.12
    ),
    amount = 10,
    convertedAmount = None,
    fromCurrency = "EUR",
    toCurrency = "USD"
  )

  val stateSignal: Signal[State] = actionStream.events.map { values => 
      dom.console.log(values)
      values
    }.foldLeft(freshState) {
    case (currentState, ChangeAmount(amount)) => currentState.copy(amount = amount)

    case (currentState, SelectFromCurrency(currency)) => currentState.copy(fromCurrency = currency)

    case (currentState, SelectToCurrency(currency)) => currentState.copy(toCurrency = currency)

    case (currentState, _: ConvertAmount.type) => 
      val rates = StringDictionary(currentState.rates.toSeq *)
      val options = CashifyOptions()
        .setRates(rates)
        .setFrom(currentState.fromCurrency)
        .setTo(currentState.toCurrency)

      val cashify = new Cashify(options)
      val result = cashify.convert(currentState.amount)
      currentState.copy(convertedAmount = Some(result))
  }

  val ratesSignal = stateSignal.map(_.rates)
  val amountSignal = stateSignal.map(_.amount)
  val convertedAmountSignal = stateSignal.map(_.convertedAmount)
  val fromCurrencySignal = stateSignal.map(_.fromCurrency)
  val toCurrencySignal = stateSignal.map(_.toCurrency)

  def ratesSplitter(currentValue: Signal[String]) = children <-- ratesSignal.map(_.toSeq).split(_._1) { case (_, _, rateSignal) => 
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
        onChange.mapToValue.map(value => SelectFromCurrency(value)) --> actionStream.writer
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
        onChange.mapToValue.map(value => SelectToCurrency(value)) --> actionStream.writer
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
}