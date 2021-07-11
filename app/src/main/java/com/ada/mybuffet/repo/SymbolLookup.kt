package com.ada.mybuffet.repo

/**
 * @author Mario Eberth
 * this class is used for the api call to get the name of the share
 */
data class SymbolLookupResponse(
    val count: Int,
    val result: List<SymbolLookupEntry>
)

/**
 * @author Mario Eberth
 * this class is used for the api call and is one entry of the SymbolLookupResponse result set
 */
data class SymbolLookupEntry(
    val description: String,
    val displaySymbol: String,
    val symbol: String,
    val type: String
)