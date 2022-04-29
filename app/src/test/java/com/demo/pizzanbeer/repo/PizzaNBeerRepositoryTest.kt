package com.demo.pizzanbeer.repo

import com.demo.pizzanbeer.db.AppDatabase
import com.demo.pizzanbeer.model.Businesses
import com.demo.pizzanbeer.model.YelpResponse
import com.demo.pizzanbeer.network.YelpApi
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.logging.Logger

class PizzaNBeerRepositoryTest {

    private lateinit var pizzaNBeerRepository: PizzaNBeerRepository

    @RelaxedMockK
    private lateinit var yelpApi: YelpApi
    @RelaxedMockK
    private lateinit var appDatabase: AppDatabase
    @RelaxedMockK
    private lateinit var logger :Logger

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        pizzaNBeerRepository = PizzaNBeerRepository(appDatabase, yelpApi, logger)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun should_VerifySuccessfulCallForBeerApi_When_ResponseIsNotNull() = runTest {
        val yelpResponse = YelpResponse(12, createFakeList())
        coEvery { yelpApi.getBeerBusinesses() } returns yelpResponse
        pizzaNBeerRepository.makeBeerApiCall()

        coVerify (exactly = 1) { yelpApi.getBeerBusinesses() }
        verify (exactly = 1) { logger.info("Call Successful") }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun should_VerifySuccessfulFailedForBeerApi_When_ResponseIsNull() = runTest {
        coEvery { yelpApi.getBeerBusinesses() } returns null
        pizzaNBeerRepository.makeBeerApiCall()

        coVerify (exactly = 1) { yelpApi.getBeerBusinesses() }
        verify (exactly = 1) { logger.info("Response is null.") }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun should_VerifySuccessfulCallForPizzaApi_When_ResponseIsNotNull() = runTest {
        val yelpResponse = YelpResponse(12, createFakeList())
        coEvery { yelpApi.getPizzaBusinesses() } returns yelpResponse
        pizzaNBeerRepository.makePizzaApiCall()

        coVerify (exactly = 1) { yelpApi.getPizzaBusinesses() }
        verify (exactly = 1) { logger.info("Call Successful") }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun should_VerifySuccessfulFailedForPizzaApi_When_ResponseIsNull() = runTest {
        coEvery { yelpApi.getPizzaBusinesses() } returns null
        pizzaNBeerRepository.makePizzaApiCall()

        coVerify (exactly = 1) { yelpApi.getPizzaBusinesses() }
        verify (exactly = 1) { logger.info("Response is null.") }
    }

    private fun createFakeList(): MutableList<Businesses> {
        val businesses = Businesses(1.9, "", "", "", "", 23.0, emptyList())
        val list = mutableListOf<Businesses>()
        list.add(businesses)
        return list
    }
}