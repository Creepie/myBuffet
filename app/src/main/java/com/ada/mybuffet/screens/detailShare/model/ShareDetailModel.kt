package com.ada.mybuffet.screens.detailShare.model

import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.repo.SymbolPrice
import java.io.IOException

class ShareDetailModel {

    suspend fun getCurrentPrice(symbol: String) : SymbolPrice?{
        val url = "https://finnhub.io/api/v1/quote?symbol=${symbol}&token=sandbox_c2vgcniad3i9mrpv9cn0"
        return try {
            FinnhubApi.retrofitService.getCurrentPrice(url)
        } catch (networkError: IOException) {
            null
        }
    }

}