package com.ada.mybuffet.screens.detailShare.model

data class OverviewData(
    val purchaseSum: Double = 0.0,
    val purchaseFeeSum: Double = 0.0,
    val purchaseCount: Int = 0,
    val saleSum: Double = 0.0,
    val saleFeeSum: Double = 0.0,
    val saleCount: Int = 0,
    val feeSum: Double = 0.0,
    val dividendSum: Double = 0.0,
    val currentWorth: Double = 0.0,
)