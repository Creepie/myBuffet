package com.ada.mybuffet.repo

/**
 * @author Mario Eberth
 * this class gets used when the worker calls the symbols of an stock and save theme to firebase
 */
data class IndexSymbols(
    val symbol: String = "",
    val constituents: List<String> = ArrayList<String>()
)
