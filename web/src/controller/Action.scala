package web.controller

import Action.*

object Action {
  enum FromTo:
    case From, To

  case class ChangeAmount(amount: Double)
  case class SelectCurrency(currency: String, fromTo: FromTo)
  case object ConvertAmount
}

type Action = ChangeAmount | SelectCurrency | ConvertAmount.type
