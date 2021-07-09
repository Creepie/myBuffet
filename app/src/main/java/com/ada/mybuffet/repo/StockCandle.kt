package com.ada.mybuffet.repo

data class StockCandle(
    val o: List<Double>,
    val h: List<Double>,
    val l: List<Double>,
    val c: List<Double>,
    val v: List<Int>,
    val t: List<Int>,
    val s: String
)
