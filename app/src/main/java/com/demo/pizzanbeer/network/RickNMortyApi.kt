package com.demo.pizzanbeer.network

import com.demo.pizzanbeer.model.Root
import retrofit2.http.GET

interface RickNMortyApi {

    @GET("api/character/")
    suspend fun getInfo(): Root
}