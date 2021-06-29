package com.ada.mybuffet.screens.detailShare.repo

import android.util.Log
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*

class ShareDetailRepository : IShareDetailRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val TAG = "SHARE_DETAIL_REPOSITORY"


    override suspend fun getPurchases(stockId: String): Flow<Resource<MutableList<Purchase>>> =
        callbackFlow {
            val userid = user!!.uid
            val docRef = firestore.collection("users").document(userid).collection("shares")
                .document(stockId).collection("purchases")
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Current data: available")

                    val purchaseList = mutableListOf<Purchase>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
                        val purchase = doc.toObject(Purchase::class.java)
                        if (purchase != null && purchase.sharePrice.isNotEmpty()) {
                            purchaseList.add(purchase)
                        }
                    }

                    // offer for flow (offer method is now deprecated, using trySend instead)
                    trySend(Resource.Success(purchaseList)).isSuccess
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }

            // close flow channel if not in use to avoid leaks
            awaitClose {
                subscription.remove()
            }
        }

    override suspend fun getSales(stockId: String): Flow<Resource<MutableList<SaleItem>>> =
        callbackFlow {
            val userid = user!!.uid
            val docRef = firestore.collection("users").document(userid).collection("shares")
                .document(stockId).collection("sales")
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Current data: available")

                    val saleList = mutableListOf<SaleItem>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
                        val saleItem = doc.toObject(SaleItem::class.java)
                        if (saleItem != null && saleItem.sharePrice.isNotEmpty()) {
                            saleList.add(saleItem)
                        }
                    }

                    // offer for flow (offer method is now deprecated, using trySend instead)
                    trySend(Resource.Success(saleList)).isSuccess
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }

            // close flow channel if not in use to avoid leaks
            awaitClose {
                subscription.remove()
            }
        }

    override suspend fun <T: Any> deleteItem(
        stockId: String,
        item: T
    ): Resource<T> {
        val userid = user!!.uid
        val itemId = when(item) {
            is Purchase -> item.id
            is SaleItem -> item.id
            else -> return  Resource.Failure(Exception())
        }
        val pathName = when(item) {
            is Purchase -> "purchases"
            is SaleItem -> "sales"
            else -> return  Resource.Failure(Exception())
        }
        val docRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockId).collection(pathName).document(itemId)
        docRef.delete().await()
        return Resource.Success(item)
    }

    override suspend fun <T: Any> addItem(
        stockId: String,
        item: T
    ): Resource<T> {
        val userid = user!!.uid
        val itemId = when(item) {
            is Purchase -> item.id
            is SaleItem -> item.id
            else -> return  Resource.Failure(Exception())
        }
        val pathName = when(item) {
            is Purchase -> "purchases"
            is SaleItem -> "sales"
            else -> return  Resource.Failure(Exception())
        }
        val docRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockId).collection(pathName).document(itemId)
        docRef.set(item).await()
        return Resource.Success(item)
    }
}