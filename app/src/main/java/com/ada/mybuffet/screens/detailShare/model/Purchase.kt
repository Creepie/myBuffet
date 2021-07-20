package com.ada.mybuffet.screens.detailShare.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.Date

/**
 *  * @author Paul Pfisterer
 * Represents one Purchase in the database
 */
@Parcelize
data class Purchase (
    val date: Date? = null,
    val fees: String = "",
    val shareNumber: Int = 0,
    val sharePrice: String ="",
    @DocumentId
    val id: String = "",
        ) : Parcelable {

    /**
     * Get the value of the purchase by multiplying the count with the share price
     */
    fun getValue(): Double {
        try {
            return sharePrice.toDouble() * shareNumber
        } catch (e: NumberFormatException) {
            Log.w("Purchase", "Errror occured while calculating the value of purchase $id", e)
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