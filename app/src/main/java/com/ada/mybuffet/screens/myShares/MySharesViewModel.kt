package com.ada.mybuffet.screens.myShares

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.screens.myShares.repo.IShareItemProvider
import com.ada.mybuffet.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class MySharesViewModel(private val shareItemProvider: IShareItemProvider) : ViewModel() {


    val fetchShareItemList = liveData(Dispatchers.IO) {
        try {
            val shareItemList = shareItemProvider.getShareItems()
            emit(shareItemList)
        } catch (e: Exception) {
            emit(Resource.Failure<Throwable>(e.cause!!))
        }
    }

    /*
    private var firestore: FirebaseFirestore

    private var _shareItems = MutableLiveData<ArrayList<ShareItem>>()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenForFirestoreData()
    }

    private fun listenForFirestoreData() {
        val userid = FirebaseAuth.getInstance().currentUser?.uid

        if (userid != null) {
            val docRef = firestore.collection("users").document(userid).collection("shares")
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MySharesViewModelTag", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d("MySharesViewModelTag", "Current data: ${snapshot}")
                    val firestoreShareItems = ArrayList<ShareItem>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
                        val shareItem = doc.toObject(ShareItem::class.java)
                        if (shareItem != null) {
                            firestoreShareItems.add(shareItem)
                        }
                    }
                    _shareItems.value = firestoreShareItems
                } else {
                    Log.d("MySharesViewModelTag", "Current data: null")
                }
            }
        }
    }

    internal var shareItems: MutableLiveData<ArrayList<ShareItem>>
        get() { return _shareItems }
        set(value) { _shareItems = value }
    */
}