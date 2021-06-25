package com.ada.mybuffet.repo

data class Dividends(
    val data: List<Dividend>,
    val symbol: String
)

data class Dividend(
    val amount: Double,
    val exDate: String
)
