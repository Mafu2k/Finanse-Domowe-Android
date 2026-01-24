package com.example.finanse.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NbpApi {
    @GET("exchangerates/tables/A")
    suspend fun getExchangeRates(@Query("format") format: String = "json"): List<NbpTable>
}

data class NbpTable(
    val table: String,
    val no: String,
    val effectiveDate: String,
    val rates: List<NbpRate>
)

data class NbpRate(
    val currency: String,
    val code: String,
    val mid: Double
)
