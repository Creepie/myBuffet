package com.ada.mybuffet.repo

/**
 * @author Mario Eberth
 * this class is to parse the data from the api which contains the prices of a share
 */
data class SymbolPrice(
    val c: Double,
    val h: Double,
    val l: Double,
    val o: Double,
    val pc: Double,
    val t: Int
)