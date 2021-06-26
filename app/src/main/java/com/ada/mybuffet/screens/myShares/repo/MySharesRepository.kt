package com.ada.mybuffet.screens.myShares.repo

import android.util.Log
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MySharesRepository : IMySharesRepository {

    private val TAG = "MY_SHARES_REPOSITORY"
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser


    override suspend fun getShareItemsFromDB(): Resource<MutableList<ShareItem>> {
        val userid = FirebaseAuth.getInstance().currentUser!!.uid

        val shareItemList = mutableListOf<ShareItem>()

        val docRef = firestore.collection("users").document(userid).collection("shares")

        val resultList = firestore.collection(docRef.path).get().await()

        for (document in resultList) {
            val shareItem = document.toObject(ShareItem::class.java)
            if (shareItem != null) {
                shareItemList.add(shareItem)
            }
        }

        return Resource.Success(shareItemList)
    }
}