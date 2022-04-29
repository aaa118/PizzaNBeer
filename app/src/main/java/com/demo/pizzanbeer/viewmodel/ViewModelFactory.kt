package com.demo.pizzanbeer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.pizzanbeer.repo.PizzaNBeerRepository

class ViewModelFactory(private val pizzaNBeerRepository: PizzaNBeerRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ListOfBusinessesViewModel::class.java)) {
            ListOfBusinessesViewModel(pizzaNBeerRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
