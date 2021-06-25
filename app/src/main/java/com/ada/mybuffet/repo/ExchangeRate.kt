package com.ada.mybuffet.repo

data class ExchangeRate(
    val base: String,
    val quote: Map<String,Double>
)
