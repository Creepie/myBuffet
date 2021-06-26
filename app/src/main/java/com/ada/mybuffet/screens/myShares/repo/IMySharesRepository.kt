package com.ada.mybuffet.screens.myShares.repo

import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource

interface IMySharesRepository {
    suspend fun getShareItemsFromDB(): Resource<MutableList<ShareItem>>
}