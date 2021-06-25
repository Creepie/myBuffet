package com.ada.mybuffet.repo

class StockShare(
    var name: String? = null,
    var dividends: Dividends? = null,
    val symbol: String,
    var curPrice: Double? = null
) {
}