package com.ada.mybuffet.screens.myShares

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ada.mybuffet.screens.myShares.repo.IShareItemProvider

// This factory is used to instantiate the view model with parameters
class MySharesViewModelFactory(private val shareItemProvider: IShareItemProvider) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IShareItemProvider::class.java).newInstance(shareItemProvider)
    }

}