package com.ada.mybuffet.repo

data class SymbolPressResponse(
    val symbol: String,
    val majorDevelopment: List<SymbolPressEntry>
)

data class SymbolPressEntry(
    val symbol: String,
    val datetime: String,
    val headline: String,
    val description: String
)