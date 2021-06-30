package com.ada.mybuffet.screens.detailShare.model

import android.util.Log
import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.repo.SymbolPressResponse
import com.ada.mybuffet.repo.SymbolPrice
import java.io.IOException

class ShareDetailModel {

    suspend fun getCurrentPrice(symbol: String) : SymbolPrice?{
        val url = "https://finnhub.io/api/v1/quote?symbol=${symbol}&token=sandbox_c2vgcniad3i9mrpv9cn0"

        try {
            Log.i("API_CURRENT_WORTH","2. Model API Call started")
            val value =  FinnhubApi.retrofitService.getCurrentPrice(url)
            Log.i("API_CURRENT_WORTH","3. Model API Call successful")
            return value
        } catch (networkError: IOException){
            Log.i("API_CURRENT_WORTH","fetchNews Error with code: ${networkError.message}")
            return null;
        }
    }

}