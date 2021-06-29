package com.ada.mybuffet.screens.myShares

import android.view.View
import com.ada.mybuffet.screens.myShares.model.ShareItem

interface MySharesRecyclerVIewClickListener {
    fun onCardClicked(shareItem: ShareItem)
}