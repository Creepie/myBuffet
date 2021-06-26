package com.ada.mybuffet.screens.myShares.repo

import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource

class ShareItemProvider(private val mySharesRepo: IMySharesRepository) : IShareItemProvider {

    override suspend fun getShareItems(): Resource<MutableList<ShareItem>> {
        return mySharesRepo.getShareItemsFromDB()
    }
}