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

class ShareDetailViewModel(
    private val shareDetailRepo: IShareDetailRepository,
    private val shareItem: ShareItem
) : ViewModel() {

    private val shareDetailModel = ShareDetailModel()

    //Fetch Lists
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


    //Fetch current Worth from API
    private val fetchCurrentHoldingsWorth = liveData(Dispatchers.IO) {
        try {
            Log.i("API_CURRENT_WORTH","1. View Model API Call started")
            val symbolPrice = shareDetailModel.getCurrentPrice(shareItem.stockSymbol)
            Log.i("API_CURRENT_WORTH","4. View Model API Call successful")
            if (symbolPrice != null) {
                Log.i("API_CURRENT_WORTH","5. View Model API Call not null")
                emit(Resource.Success<SymbolPrice>(symbolPrice))
            } else {
                emit(Resource.Failure<Exception>(Exception()))
            }

        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

    val overviewData = MediatorLiveData<Resource<OverviewData>>().apply {
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
        addSource(fetchCurrentHoldingsWorth) {
            if (it is Resource.Success) {
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
        }

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


    fun <T : Any> onItemSwiped(item: T) = liveData(Dispatchers.IO) {
        try {
            emit(shareDetailRepo.deleteItem(shareItem.shareItemId, item))
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }

    }

    fun <T : Any> onUndoDeleteItem(item: T) = liveData(Dispatchers.IO) {
        try {
            emit(shareDetailRepo.addItem(shareItem.shareItemId, item))
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))

        }

    }

}