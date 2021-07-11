package com.ada.mybuffet.screens.stocksDetail

import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.repo.StockCandle
import java.io.IOException

/**
 * @author Mario Eberth
 */
class StocksDetailModel {

    suspend fun loadCandle(symbol: String, from: String, to: String): StockCandle? {
        return try {
            FinnhubApi.retrofitService.getCandle(symbol,from,to)
        }catch (networkError: IOException){
            null
        }
    }
}