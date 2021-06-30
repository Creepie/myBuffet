package com.ada.mybuffet.screens.stocks

import com.ada.mybuffet.screens.myShares.model.ShareItem

interface StocksRecyclerViewClickListener {
    fun onCardClicked(shareItem: ShareItem)
}