package com.ada.mybuffet.screens.stocks

import com.ada.mybuffet.repo.StockShare

/**
 * @author Mario Eberth
 */
interface StocksRecyclerViewClickListener {
    fun onCardClicked(shareItem: StockShare)
}