package com.ada.mybuffet.screens.detailShare.repo

import android.util.Log
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ShareDetailRepository : IShareDetailRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val TAG = "SHARE_DETAIL_REPOSITORY"


    override suspend fun getPurchases(): Flow<Resource<MutableList<Purchase>>> =
        callbackFlow {
            val userid = user!!.uid
            val docRef = firestore.collection("users").document(userid).collection("shares").document("DS13Qiic8WB5cdZRLNGb").collection("purchases")
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Current data: available")

                    val purchaseList = mutableListOf<Purchase>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
                        val purchase = doc.toObject(Purchase::class.java)
                        if (purchase != null) {
                            purchase.id = doc.id
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
            awaitClose{
                subscription.remove()
            }
        }

    override suspend fun getSales(): Flow<Resource<MutableList<SaleItem>>> =
        callbackFlow {
            val userid = user!!.uid
            val docRef = firestore.collection("users").document(userid).collection("shares").document("DS13Qiic8WB5cdZRLNGb").collection("sales")
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Current data: available")

                    val saleList = mutableListOf<SaleItem>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
                        val saleItem = doc.toObject(SaleItem::class.java)
                        if (saleItem != null) {
                            saleItem.id = doc.id
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
            awaitClose{
                subscription.remove()
            }
        }
}