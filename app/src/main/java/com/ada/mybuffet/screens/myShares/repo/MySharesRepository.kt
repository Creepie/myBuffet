package com.ada.mybuffet.screens.myShares.repo

import android.util.Log
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MySharesRepository : IMySharesRepository {

    private val TAG = "MY_SHARES_REPOSITORY"
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser


    override suspend fun getShareItemsFromDB(): Flow<Resource<MutableList<ShareItem>>> = callbackFlow {
        // create reference to the collection in firestore
        val userid = FirebaseAuth.getInstance().currentUser!!.uid
        val docRef = firestore.collection("users").document(userid).collection("shares")

        // create subscription which listens to database changes
        val subscription = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
            } else if (snapshot != null) {
                Log.d(TAG, "Listen successful")

                val shareItemList = mutableListOf<ShareItem>()
                val documents = snapshot.documents
                documents.forEach { doc ->
                    val shareItem = doc.toObject(ShareItem::class.java)
                    if (shareItem != null) {
                        shareItemList.add(shareItem)
                    }
                }

                // offer for flow (offer method is now deprecated, using trySend instead)
                trySend(Resource.Success(shareItemList)).isSuccess
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