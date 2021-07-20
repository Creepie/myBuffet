package com.ada.mybuffet.screens.detailShare.repo

import com.ada.mybuffet.repo.SymbolPrice
import com.ada.mybuffet.screens.detailShare.model.DividendItem
import com.ada.mybuffet.screens.detailShare.model.FeeItem
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * @author Paul Pfisterer
 * Interface for the Share Detail Repository.
 */
interface IShareDetailRepository {
    /**
     * Gets a list of all purchases from the db from the stock, with the passed id
     * Returns the list as encapsulated in a Resource Flow
     */
    suspend fun getPurchases(stockId: String): Flow<Resource<MutableList<Purchase>>>
    /**
     * Gets a list of all sales from the db from the stock, with the passed id
     * Returns the list as Flow, encapsulated in a Resource
     */
    suspend fun getSales(stockId: String): Flow<Resource<MutableList<SaleItem>>>
    /**
     * Gets a list of all fees from the db from the stock, with the passed id
     * Returns the list as Flow, encapsulated in a Resource
     */
    suspend fun getFees(stockId: String): Flow<Resource<MutableList<FeeItem>>>
    /**
     * Gets a list of all dividends from the db from the stock, with the passed id
     * Returns the list as Flow, encapsulated in a Resource
     */
    suspend fun getDividends(stockId: String): Flow<Resource<MutableList<DividendItem>>>
    /**
     * Gets the current price of a stock with the passed symbol
     */
    suspend fun getCurrentPrice(symbol: String) : SymbolPrice?
    /**
     * Deletes an item from the db. The item can be a purchase, sale, fee or dividend.
     * Depending on the type, the item is deleted from the correct collection.
     */
    suspend fun <T: Any> deleteItem(stockId: String, item: T) : Resource<T>

    /**
     * Adds an item to the db. The item can be a purchase, sale, fee or dividend.
     * Depending on the type, the item is added to the correct collection.
     * This is used for undoing a deletion
     */
    suspend fun <T: Any> addItem(stockId: String, item: T) : Resource<T>

    /**
     * This function updates the price of the stock in the db.
     * The price and the price-percentage of the stock with the stockId are set to
     * the values stored in the SymbolPrice
     */
    suspend fun updatePriceInDB(stockId: String, price: SymbolPrice)
}