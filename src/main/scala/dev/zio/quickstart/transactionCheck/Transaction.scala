package dev.zio.quickstart.transactionCheck

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Transaction(src: String, dst: String, amount: Int)

object Transaction:
  given JsonEncoder[Transaction] =
    DeriveJsonEncoder.gen[Transaction]
  given JsonDecoder[Transaction] =
    DeriveJsonDecoder.gen[Transaction]
