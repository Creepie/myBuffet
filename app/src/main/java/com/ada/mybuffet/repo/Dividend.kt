package com.ada.mybuffet.repo

data class Dividend(
    val symbol: String,
    val date: String,
    val amount: Double,
    val adjustedAmount: Double,
    val payDate: String,
    val recordDate: String,
    val declarationDate: String,
    val currency: String
)
