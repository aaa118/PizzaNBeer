package com.demo.pizzanbeer.repo

import com.demo.pizzanbeer.db.AppDatabase
import com.demo.pizzanbeer.model.Businesses
import com.demo.pizzanbeer.model.YelpResponse
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
        processResponseAndSaveToDb(pizzaResponse, PIZZA)
    }

    suspend fun makeBeerApiCall() {
        val beerResponse = yelpApi.getBeerBusinesses()
        processResponseAndSaveToDb(beerResponse, BEER)
    }

    private suspend fun processResponseAndSaveToDb(yelpResponse: YelpResponse?, alias: String) {
        if (yelpResponse != null) {
            yelpResponse.businesses.forEach {
                it.categories[0].alias = alias
            }
            saveListToDb(yelpResponse.businesses)
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

    companion object {
        const val BEER = "Beer"
        const val PIZZA = "Pizza"
    }
}