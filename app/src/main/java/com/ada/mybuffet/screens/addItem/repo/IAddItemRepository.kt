package com.ada.mybuffet.screens.addItem.repo

import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource

/**
 * @author Paul Pfisterer
 * Interface for the Add Item Repository.
 */
interface IAddItemRepository {
    /**
     * Adds an item to the according collection of the database
     * The type of the item can be purchase, sale, fee or dividend
     * The id of the stock is passed
     */
    suspend fun <T: Any> addItem(stockItem: ShareItem, item: T) : Resource<T>

    /**
     * Adds a purchase to the purchase collection of the database.
     * The id is unknown. Instead the symbol of the stock is passed
     */
    suspend fun addPurchaseWithoutId(stockSymbol: String, purchase: Purchase) : Resource<Purchase>
}