package com.ada.mybuffet.repo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Mario Eberth
 */
@Parcelize
data class Dividends(
    val data: List<Dividend> = ArrayList(),
    val symbol: String = ""
) : Parcelable

/**
 * @author Mario Eberth
 */
@Parcelize
data class Dividend(
    val amount: Double = 0.0,
    val exDate: String = ""
) : Parcelable
