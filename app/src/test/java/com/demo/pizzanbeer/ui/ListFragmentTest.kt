package com.demo.pizzanbeer.ui

import androidx.core.view.isVisible
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import com.demo.pizzanbeer.model.YelpResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ListFragmentTest {

    val scenario = launchFragmentInContainer<ListFragment>()

    @get:Rule
    var injectionRue = AndroidInjectionTestRule()

    @Before
    fun setUp() {
    }

    @Test
    fun should_VerifySuccessfulCallForBeerApi_When_ResponseIsNotNull()  {
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment {
            assertEquals(it.fragmentItemListBinding.list.isVisible, true)
        }

    }

}