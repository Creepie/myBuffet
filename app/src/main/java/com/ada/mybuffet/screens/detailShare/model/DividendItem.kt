package com.ada.mybuffet.screens.detailShare.model

import com.google.firebase.firestore.DocumentId
import java.text.SimpleDateFormat
import java.util.*

/**
 *  * @author Paul Pfisterer
 * Represents one Dividend in the database
 */
data class DividendItem (
    val amount: String = "",
    val date: Date? = null,
    val number: Int = 0,
    @DocumentId
    val id: String = "",
        ) {
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