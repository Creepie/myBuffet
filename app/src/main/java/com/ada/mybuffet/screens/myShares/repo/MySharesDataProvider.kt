package com.ada.mybuffet.screens.myShares.repo

import com.ada.mybuffet.screens.myShares.model.PortfolioValueByDate
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

class MySharesDataProvider(private val mySharesRepo: IMySharesRepository) : IMySharesDataProvider {

    override suspend fun getShareItems(): Flow<Resource<MutableList<ShareItem>>> {
        return mySharesRepo.getShareItemsFromDB()
    }

    override suspend fun getTotalPortfolioValueHistory(): Flow<Resource<MutableList<PortfolioValueByDate>>> {
        return mySharesRepo.getTotalPortfolioValueHistoryFromDB()
    }

    override suspend fun getProfitLossOverviewData(): Flow<Resource<HashMap<String, BigDecimal>>> {
        return mySharesRepo.getProfitLossOverviewDataFromDB()
    }
}