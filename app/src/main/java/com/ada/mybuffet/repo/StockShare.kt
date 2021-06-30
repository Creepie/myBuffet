package com.ada.mybuffet.repo

class StockShare(
    var name: String = "",
    var dividends: Dividends? = null,
    val symbol: String = "",
    var curPrice: Double? = 0.0
) {
}