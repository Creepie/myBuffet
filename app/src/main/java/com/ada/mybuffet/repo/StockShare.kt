package com.ada.mybuffet.repo

class StockShare(
    val name: String? = null,
    var dividends: Dividends? = null,
    val symbol: String,
    val curPrice: Double? = null
) {
}