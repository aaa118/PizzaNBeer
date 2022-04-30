package com.demo.pizzanbeer.network

import com.demo.pizzanbeer.model.YelpResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface YelpApi {

    @Headers("Authorization: Bearer $API_KEY")
    @GET("v3/businesses/search?term=pizza&location=111%20Sutter%20Street%20San%20Francisco,%20CA")
    suspend fun getPizzaBusinesses(): YelpResponse?

    @Headers("Authorization: Bearer $API_KEY")
    @GET("v3/businesses/search?term=beer&location=111%20Sutter%20Street%20San%20Francisco,%20CA")
    suspend fun getBeerBusinesses(): YelpResponse?

    companion object {
        const val API_KEY =
            "2ROaa2Rh9qu3WVTCms8FoVE4mSfHQHC7QJua95-kKT-PqzIlLSrs4tmHVdtdFw_66-JNfRiJmbCByHTvFNy5dQq-tpfS4FrPpupIzKlgELR3br-r5trpeFhrCRgwWnYx"
    }
}