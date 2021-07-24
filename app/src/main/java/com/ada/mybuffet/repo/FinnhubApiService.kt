package com.ada.mybuffet.repo

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * @author Mario Eberth
 * API KEY SANDBOX (60 Calls / min)
 * Sandbox: sandbox_c2vgcniad3i9mrpv9cn0
 * normal: c2vgcniad3i9mrpv9cmg
 */

private const val Base_URL = "https://finnhub.io/api/v1/"

private const val token = "c2vgcniad3i9mrpv9cmg"
private const val sandboxToken = "sandbox_c2vgcniad3i9mrpv9cn0"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
    .baseUrl(Base_URL)
    .build()

interface FinnhubApiService{

    /**
     * this function is to get the right name of the share
     * https://finnhub.io/docs/api/symbol-search
     */
    @GET("search?token=$token")
    suspend fun getName(@Query("q") symbol: String): SymbolLookupResponse

    /**
     * this function is to get the current price of the share
     * https://finnhub.io/docs/api/quote
     */
    @GET("quote?token=$token")
    suspend fun getCurrentPrice(@Query("symbol") symbol: String): SymbolPrice

    /**
     * this function is to get press releases of a share
     * https://finnhub.io/docs/api/press-releases
     */
    @GET
    suspend fun getPressNews(@Url url: String): SymbolPressResponse
   // @GET("press-releases?token=$token")
   // suspend fun getPressNews(@Query("symbol") symbol: String): SymbolPressResponse

    /**
     * this function is to get the current exchangeRate
     * in the request you can set a base currency like USD
     */
    @GET
    suspend fun getExchangeRate(@Url url: String): ExchangeRate

    /**
     * this function is to get the symbols of company's in a specific index like DAX
     * in the request you can set the index you are looking for
     */
    @GET("index/constituents?token=$token")
    suspend fun getIndexSymbols(@Query("symbol") index: String): IndexSymbols

    /**
     * this function is to get the dividends of a specific company
     */
    @GET("stock/dividend2?token=$sandboxToken")
    suspend fun getDividends(@Query("symbol") symbol: String): Dividends

    /**
     * this function is to get candles of a specific company in a specific time range
     */
    @GET("stock/candle?token=$sandboxToken&resolution=1")
    suspend fun getCandle(@Query("symbol") symbol: String,@Query("from") from: String, @Query("to") to: String): StockCandle

}

object FinnhubApi{
    val retrofitService: FinnhubApiService by lazy {
        retrofit.create(FinnhubApiService::class.java)
    }
}