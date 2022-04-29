package com.demo.pizzanbeer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class YelpResponse(val total: Int, val businesses: List<Businesses>)

@Entity
data class Businesses(
    val rating: Double,
    val price: String?,
    val name: String?,
    val phone: String?,
    @PrimaryKey
    val id: String,
    val distance: Double,
    val categories: List<Categories>,
    val image_url: String,
)

data class Categories (var alias: String, val title: String)
