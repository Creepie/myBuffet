package com.ada.mybuffet.repo

data class SymbolLookupResponse(
    val count: Int,
    val result: List<SymbolLookupEntry>
)

data class SymbolLookupEntry(
    val description: String,
    val displaySymbol: String,
    val symbol: String,
    val type: String
)