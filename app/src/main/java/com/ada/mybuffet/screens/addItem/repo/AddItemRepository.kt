package com.ada.mybuffet.screens.addItem.repo

import com.ada.mybuffet.screens.detailShare.model.DividendItem
import com.ada.mybuffet.screens.detailShare.model.FeeItem
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class AddItemRepository : IAddItemRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val TAG = "ADD_ITEM_REPOSITORY"


    override suspend fun <T: Any> addItem(
        stockId: String,
        item: T
    ): Resource<T> {
        val userid = user!!.uid
        val pathName = when(item) {
            is Purchase -> "purchases"
            is SaleItem -> "sales"
            is FeeItem -> "fees"
            is DividendItem -> "dividends"
            else -> return  Resource.Failure(Exception())
        }
        val docRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockId).collection(pathName)
        docRef.add(item).await()
        return Resource.Success(item)
    }
}