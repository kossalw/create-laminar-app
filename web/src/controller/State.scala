package web.controller

import com.raquo.laminar.api.L.*

import typings.cashify
import cashify.mod.Cashify
import cashify.anon.PartialOptions as CashifyOptions

import org.scalablytyped.runtime.StringDictionary

import Action.*
import FromTo.*

object State {
  val actionStream: EventBus[Action] = new EventBus[Action]

  case class State(
    rates: Map[String, Double],
    amount: Double,
    convertedAmount: Option[Double],
    fromCurrency: String,
    toCurrency: String
  )

  private val freshState = State(
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

  val stateSignal: Signal[State] = actionStream.events.foldLeft(freshState) {
    case (currentState, ChangeAmount(amount)) => currentState.copy(amount = amount)

    case (currentState, SelectCurrency(currency, From)) => currentState.copy(fromCurrency = currency)

    case (currentState, SelectCurrency(currency, To)) => currentState.copy(toCurrency = currency)

    case (currentState, _: ConvertAmount.type) =>
      val rates = StringDictionary(currentState.rates.toSeq*)
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
}
