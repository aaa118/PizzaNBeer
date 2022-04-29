package com.demo.pizzanbeer.di

import com.demo.pizzanbeer.network.YelpApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): YelpApi {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
        return retrofit.create(YelpApi::class.java)
    }

    companion object {
        const val BASE_URL = "https://api.yelp.com/"
    }
}