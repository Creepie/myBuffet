package com.ada.mybuffet.screens.detailShare.viewModel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.ada.mybuffet.repo.SymbolPrice
import com.ada.mybuffet.screens.detailShare.model.*
import com.ada.mybuffet.screens.detailShare.repo.IShareDetailRepository
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import java.lang.Exception

/**
 * @author Paul Pfisterer
 * View Model for the ShareDetail View
 * Takes the shareDetailRepo and the shareItem(Representing the current stock) as arguments
 */
class ShareDetailViewModel(
    private val shareDetailRepo: IShareDetailRepository,
    private val shareItem: ShareItem
) : ViewModel() {

    /**
     * Fetches the purchase list. Returns a Resource.Success with a list from the repo on success.
     */
    val fetchPurchaseItemList = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            shareDetailRepo.getPurchases(shareItem.shareItemId).collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

    /**
     * Fetches the sales list. Returns a Resource.Success with a list from the repo on success.
     */
    val fetchSaleItemList = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            shareDetailRepo.getSales(shareItem.shareItemId).collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

    /**
     * Fetches the fees list. Returns a Resource.Success with a list from the repo on success.
     */
    val fetchFeeItemList = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            shareDetailRepo.getFees(shareItem.shareItemId).collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

    /**
     * Fetches the dividends list. Returns a Resource.Success with a list from the repo on success.
     */
    val fetchDividendItemList = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            shareDetailRepo.getDividends(shareItem.shareItemId).collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }


    /**
     * Fetches the current worth of the stock from the repository.
     * Updates the values in the database if it was successful
     */
    private val fetchCurrentHoldingsWorth = liveData(Dispatchers.IO) {
        try {
            val symbolPrice = shareDetailRepo.getCurrentPrice(shareItem.stockSymbol)
            if (symbolPrice != null) {
                emit(Resource.Success<SymbolPrice>(symbolPrice))
                shareDetailRepo.updatePriceInDB(shareItem.shareItemId, symbolPrice)
            } else {
                emit(Resource.Failure<Exception>(Exception()))
            }

        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

    /**
     * Mediator Live data: Observes the liveDatas from above and returns a OverviewData Object,
     * that contains all teh necessary data for the detail overview
     * Emits a new overviewData object (value) whenever a LiveData to which it is subscribed changes
     */
    val overviewData = MediatorLiveData<Resource<OverviewData>>().apply {
        //Define all values
        var purchaseSum = 0.0
        var purchaseFeeSum = 0.0
        var purchaseCount = 0

        var saleSum = 0.0
        var saleFeeSum = 0.0
        var saleCount = 0

        var feeSum = 0.0
        var dividendSum = 0.0

        var currentWorth = 0.0
        var currentWorthPercentage = 0.0

        //Add the currentHolding livedata as source
        addSource(fetchCurrentHoldingsWorth) {
            if (it is Resource.Success) {
                //calculate price
                val price = (it.data as SymbolPrice)
                currentWorth = price.c
                currentWorthPercentage = (1-(price.pc/price.c))*100

                value = Resource.Success(
                    OverviewData(
                        purchaseSum = purchaseSum,
                        purchaseFeeSum = purchaseFeeSum,
                        purchaseCount = purchaseCount,
                        saleSum = saleSum,
                        saleFeeSum = saleFeeSum,
                        saleCount = saleCount,
                        feeSum = feeSum,
                        dividendSum = dividendSum,
                        currentWorth = currentWorth,
                        currentWorthPercentage = currentWorthPercentage
                    )
                )
            }
            if (it is Resource.Failure) {
                //If price could not be loaded, use last price from database
                currentWorth = shareItem.currentPrice.toDouble()
                currentWorthPercentage = shareItem.currentPricePercent.toDouble()

                value = Resource.Success(
                    OverviewData(
                        purchaseSum = purchaseSum,
                        purchaseFeeSum = purchaseFeeSum,
                        purchaseCount = purchaseCount,
                        saleSum = saleSum,
                        saleFeeSum = saleFeeSum,
                        saleCount = saleCount,
                        feeSum = feeSum,
                        dividendSum = dividendSum,
                        currentWorth = currentWorth,
                        currentWorthPercentage = currentWorthPercentage
                    )
                )
            }
        }

        //Add the purchases livedata as source
        addSource(fetchPurchaseItemList) {
            if (it is Resource.Success) {
                purchaseSum = 0.0
                purchaseCount = 0
                purchaseFeeSum = 0.0
                val purchases: List<Purchase> = (it.data as List<Purchase>)
                for (purchase in purchases) {
                    purchaseSum += purchase.getValue()
                    purchaseFeeSum += purchase.fees.toDouble()
                    purchaseCount += purchase.shareNumber
                }
                value = Resource.Success(
                    OverviewData(
                        purchaseSum = purchaseSum,
                        purchaseFeeSum = purchaseFeeSum,
                        purchaseCount = purchaseCount,
                        saleSum = saleSum,
                        saleFeeSum = saleFeeSum,
                        saleCount = saleCount,
                        feeSum = feeSum,
                        dividendSum = dividendSum,
                        currentWorth = currentWorth,
                        currentWorthPercentage = currentWorthPercentage
                    )
                )
            }
        }
        //Add the sales livedata as source
        addSource(fetchSaleItemList) {
            if (it is Resource.Success) {
                saleSum = 0.0
                saleCount = 0
                saleFeeSum = 0.0
                val sales: List<SaleItem> = (it.data as List<SaleItem>)
                for (sale in sales) {
                    saleSum += sale.getValue()
                    saleFeeSum += sale.fees.toDouble()
                    saleCount += sale.shareNumber
                }
                value = Resource.Success(
                    OverviewData(
                        purchaseSum = purchaseSum,
                        purchaseFeeSum = purchaseFeeSum,
                        purchaseCount = purchaseCount,
                        saleSum = saleSum,
                        saleFeeSum = saleFeeSum,
                        saleCount = saleCount,
                        feeSum = feeSum,
                        dividendSum = dividendSum,
                        currentWorth = currentWorth,
                        currentWorthPercentage = currentWorthPercentage
                    )
                )
            }
        }
        //Add the fees livedata as source
        addSource(fetchFeeItemList) {
            if (it is Resource.Success) {
                feeSum = 0.0
                val fees: List<FeeItem> = (it.data as List<FeeItem>)
                for (fee in fees) {
                    feeSum += fee.amount.toDouble()
                }
                value = Resource.Success(
                    OverviewData(
                        purchaseSum = purchaseSum,
                        purchaseFeeSum = purchaseFeeSum,
                        purchaseCount = purchaseCount,
                        saleSum = saleSum,
                        saleFeeSum = saleFeeSum,
                        saleCount = saleCount,
                        feeSum = feeSum,
                        dividendSum = dividendSum,
                        currentWorth = currentWorth,
                        currentWorthPercentage = currentWorthPercentage
                    )
                )
            }
        }
        //Add the dividends livedata as source
        addSource(fetchDividendItemList) {
            if (it is Resource.Success) {
                dividendSum = 0.0
                val dividends: List<DividendItem> = (it.data as List<DividendItem>)
                for (dividend in dividends) {
                    dividendSum += dividend.amount.toDouble()
                }
                value = Resource.Success(
                    OverviewData(
                        purchaseSum = purchaseSum,
                        purchaseFeeSum = purchaseFeeSum,
                        purchaseCount = purchaseCount,
                        saleSum = saleSum,
                        saleFeeSum = saleFeeSum,
                        saleCount = saleCount,
                        feeSum = feeSum,
                        dividendSum = dividendSum,
                        currentWorth = currentWorth,
                        currentWorthPercentage = currentWorthPercentage
                    )
                )
            }
        }
    }


    /**
     * Called by the view when an item is swiped from one of the recycler views
     * The type doesn't matter, as that is handled in the repo
     */
    fun <T : Any> onItemSwiped(item: T) = liveData(Dispatchers.IO) {
        try {
            emit(shareDetailRepo.deleteItem(shareItem.shareItemId, item))
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }

    }

    /**
     * Called by the view when a deleted item should be undone
     * The type doesn't matter, as that is handled in the repo
     */
    fun <T : Any> onUndoDeleteItem(item: T) = liveData(Dispatchers.IO) {
        try {
            emit(shareDetailRepo.addItem(shareItem.shareItemId, item))
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))

        }

    }

}