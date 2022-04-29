package com.demo.pizzanbeer.repo

import com.demo.pizzanbeer.db.AppDatabase
import com.demo.pizzanbeer.model.Businesses
import com.demo.pizzanbeer.network.RickNMortyApi
import com.demo.pizzanbeer.network.YelpApi
import kotlinx.coroutines.flow.Flow
import java.util.logging.Logger
import javax.inject.Inject

class PizzaNBeerRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val yelpApi: YelpApi,
    private val logger: Logger,
) {

    suspend fun makePizzaApiCall() {
        val pizzaResponse = yelpApi.getPizzaBusinesses()
        if (pizzaResponse != null) {
            pizzaResponse.businesses.forEach {
                it.categories[0].alias = "Pizza"
            }
            saveListToDb(pizzaResponse.businesses)
            logger.info("Call Successful")
        } else {
            logger.info("Response is null.")
        }
    }

    suspend fun makeBeerApiCall() {
        val beerResponse = yelpApi.getBeerBusinesses()
        if (beerResponse != null) {
            beerResponse.businesses.forEach {
                it.categories[0].alias = "Beer"
            }
            saveListToDb(beerResponse.businesses)
            logger.info("Call Successful")

        } else {
            logger.info("Response is null.")
        }
    }

    private suspend fun saveListToDb(listOfBusinesses: List<Businesses>) {
        appDatabase.businessesDao().insertAll(listOfBusinesses)
    }

    fun getFromDb(): Flow<List<Businesses>> {
        return appDatabase.businessesDao().getAll()
    }
}