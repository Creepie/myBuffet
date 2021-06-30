package com.ada.mybuffet.repo

data class Dividends(
    val data: List<Dividend> = ArrayList(),
    val symbol: String = ""
)

data class Dividend(
    val amount: Double = 0.0,
    val exDate: String = ""
)
