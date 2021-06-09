package com.ada.mybuffet.repo

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * API KEY SANDBOX (60 Calls / min)
 * sandbox_c2vgcniad3i9mrpv9cn0
 */

private const val Base_URL = "https://finnhub.io/api/v1/"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Base_URL)
    .build()

interface FinnhubApiService{

    /**
     * this function is to get the right name of the share
     * https://finnhub.io/docs/api/symbol-search
     */
    @GET
    suspend fun getName(@Url url: String): SymbolLookupResponse

    /**
     * this function is to get the current price of the share
     * https://finnhub.io/docs/api/quote
     */
    @GET
    suspend fun getCurrentPrice(@Url url: String): SymbolPrice

    /**
     * this function is to get press releases of a share
     * https://finnhub.io/docs/api/press-releases
     */
    @GET
    suspend fun getPressNews(@Url url: String): SymbolPressResponse

    /**
     * this function is to get candles of a share in a specific time range
     * https://finnhub.io/docs/api/stock-candles
     */
    @GET
    suspend fun getStockCandles(@Url url: String): SymbolCandles
}

object FinnhubApi{
    val retrofitService: FinnhubApiService by lazy {
        retrofit.create(FinnhubApiService::class.java)
    }
}