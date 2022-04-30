package com.demo.pizzanbeer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.demo.pizzanbeer.model.Businesses
import com.demo.pizzanbeer.repo.PizzaNBeerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class ListOfBusinessesViewModel(private val pizzaNBeerRepository: PizzaNBeerRepository) :
    ViewModel() {

    var listOfBusinessesMutableLiveData: LiveData<List<Businesses>> =
        pizzaNBeerRepository.getFromDb().asLiveData()

    fun loadApi() {
        CoroutineScope(Dispatchers.IO).async {
            pizzaNBeerRepository.makePizzaApiCall()
            pizzaNBeerRepository.makeBeerApiCall()
        }    }
}