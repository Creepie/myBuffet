package com.ada.mybuffet.screens.detailShare.model

import android.util.Log
import com.google.firebase.firestore.DocumentId
import java.lang.NumberFormatException
import java.util.*

data class SaleItem(
    val date: Date? = null,
    val fees: String = "",
    val shareNumber: Int = 0,
    val sharePrice: String ="",
    @DocumentId
    val id: String = "",
) {
    fun getValue(): Double {
        try {
            return sharePrice.toDouble() * shareNumber
        } catch (e: NumberFormatException) {
            Log.w("SaleIte", "Errror occured while calculating the value of saleItem $id", e)
        }
        return 0.00
    }
}