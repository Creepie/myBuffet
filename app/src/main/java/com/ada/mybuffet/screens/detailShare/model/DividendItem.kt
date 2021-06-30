package com.ada.mybuffet.screens.detailShare.model

import com.google.firebase.firestore.DocumentId
import java.text.SimpleDateFormat
import java.util.*

data class DividendItem (
    val amount: String = "",
    val date: Date? = null,
    val number: Int = 0,
    @DocumentId
    val id: String = "",
        ) {
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