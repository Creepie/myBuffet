package com.ada.mybuffet.screens.news

import com.ada.mybuffet.repo.StockShare
import com.ada.mybuffet.repo.SymbolPressResponse
/**
 * @author Selin Bilge
 */
interface NewsRecyclerViewClickListener {
    fun onCardClicked(shareItem: SymbolPressResponse)
}