package com.ada.mybuffet.repo

/**
 * @author Mario Eberth
 * this class is to save the different exchange rates like EUR - USD and so on
 */
data class ExchangeRate(
    val base: String,
    val quote: Map<String,Double>
)
