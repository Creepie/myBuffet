package com.ada.mybuffet.screens.myShares.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ada.mybuffet.screens.myShares.repo.IMySharesDataProvider

// This factory is used to instantiate the view model with parameters
class MySharesViewModelFactory(private val mySharesDataProvider: IMySharesDataProvider) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IMySharesDataProvider::class.java).newInstance(mySharesDataProvider)
    }

}