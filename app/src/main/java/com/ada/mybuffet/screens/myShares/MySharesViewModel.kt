package com.ada.mybuffet.screens.myShares

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MySharesViewModel : ViewModel() {

    private var firestore: FirebaseFirestore

    private var _shareItems = MutableLiveData<ArrayList<ShareItem>>()

    init {
        firestore = FirebaseFirestore.getInstance()
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

}