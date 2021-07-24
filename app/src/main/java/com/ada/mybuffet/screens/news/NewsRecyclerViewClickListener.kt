package com.ada.mybuffet.screens.news

import com.ada.mybuffet.repo.StockShare
import com.ada.mybuffet.repo.SymbolPressResponse

interface NewsRecyclerViewClickListener {
    fun onCardClicked(shareItem: SymbolPressResponse)
}