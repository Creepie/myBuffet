package com.ada.mybuffet.screens.stocks

import com.ada.mybuffet.repo.StockShare

interface StocksRecyclerViewClickListener {
    fun onCardClicked(shareItem: StockShare)
}