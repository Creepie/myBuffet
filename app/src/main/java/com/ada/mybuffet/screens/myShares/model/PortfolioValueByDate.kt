package com.ada.mybuffet.screens.myShares.model

import com.google.firebase.Timestamp

data class PortfolioValueByDate(
    val date: Timestamp = Timestamp.now(),
    val portfolioTotalValue: String = ""
)