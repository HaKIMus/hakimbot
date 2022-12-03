package com.hakim.hakimbot.gamble.exception

open class GambleException(override val message: String?) : Exception(message)

class InvalidPercentageRangeException(message: String?) : GambleException(message)
class NoBalanceException(message: String?) : GambleException(message)
class NoAmountException(message: String?) : GambleException(message)
class NotSufficientBalanceException(message: String?) : GambleException(message)
class Minimum10PercentOfBalance(message: String?) : GambleException(message)