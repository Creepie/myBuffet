package com.ada.mybuffet.screens.detailShare.model

import java.util.Date

data class Purchase (
    val date: Date? = null,
    val fees: String = "",
    val shareNumber: Int = 0,
    val sharePrice: String ="",
    var id: String = "",
        )