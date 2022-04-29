package com.demo.pizzanbeer.model

data class Root(
    val info: Info,
    val results: List<Results>
)

data class Info(
    val count: Int,
    val pages: Int,
    val next: String,
    val prev: String
)

data class Results(val id: Int, val name: String, val status: String)
