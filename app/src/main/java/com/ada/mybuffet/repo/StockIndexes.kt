package com.ada.mybuffet.repo

/**
 * @author Mario Eberth
 * this is a helper class / object which is needed to know which shares the worker should load next
 * stockIndex = which stock should be taken
 * shareIndex = which shares in a specific stock should be loaded
 */
data class StockIndexes(
    var stockIndex: Int = 0,
    var shareIndex: Int = 0
)
