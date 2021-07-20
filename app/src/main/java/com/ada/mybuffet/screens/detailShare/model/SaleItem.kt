package com.ada.mybuffet.screens.detailShare.model

import android.util.Log
import com.google.firebase.firestore.DocumentId
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*

/**
 *  * @author Paul Pfisterer
 * Represents one Sale in the database
 */
data class SaleItem(
    val date: Date? = null,
    val fees: String = "",
    val shareNumber: Int = 0,
    val sharePrice: String ="",
    @DocumentId
    val id: String = "",
) {
    /**
     * Get the value of the sale by multiplying the count with the share price
     */
    fun getValue(): Double {
        try {
            return sharePrice.toDouble() * shareNumber
        } catch (e: NumberFormatException) {
            Log.w("SaleIte", "Errror occured while calculating the value of saleItem $id", e)
        }
        return 0.00
    }

    /**
     * Get a formatted string representation of the date
     */
    fun getFormattedDate(): String {
        val pattern = "dd.MM.yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern)
        return if(date != null) {
            simpleDateFormat.format(date)
        } else {
            "unknown"
        }
    }
}