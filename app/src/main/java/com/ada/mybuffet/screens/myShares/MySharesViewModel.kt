package com.ada.mybuffet.screens.myShares

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MySharesViewModel : ViewModel() {

    private var firestore: FirebaseFirestore

    private var _mySharesModel = MutableLiveData<MySharesModel>()

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenForFirestoreData()
    }

    private fun listenForFirestoreData() {
        val userid = FirebaseAuth.getInstance().currentUser?.uid

        if (userid != null) {
            val docRef = firestore.collection("users").document(userid)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MySharesViewModelTag", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("MySharesViewModelTag", "Current data: ${snapshot.data}")
                    val model = snapshot.toObject(MySharesModel::class.java)
                    Log.d("MySharesViewModelTag", "Model data: ${model.toString()}")

                    _mySharesModel.value = model
                } else {
                    Log.d("MySharesViewModelTag", "Current data: null")
                }
            }
        }
    }

    internal var mySharesModel: MutableLiveData<MySharesModel>
        get() { return _mySharesModel }
        set(value) { _mySharesModel = value }

}