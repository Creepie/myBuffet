package com.ada.mybuffet.repo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class SymbolPressResponse(
    val symbol: String = "",
    val majorDevelopment: @RawValue List<SymbolPressEntry> = ArrayList()
) : Parcelable {

}

data class SymbolPressEntry(
    val symbol: String,
    val datetime: String? = null,
    val headline: String,
    val description: String
)