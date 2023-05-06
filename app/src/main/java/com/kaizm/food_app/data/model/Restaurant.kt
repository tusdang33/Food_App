package com.kaizm.food_app.data.model

import java.io.Serializable

data class Restaurant(
    var id: String = "",
    val name: String = "",
    val address: String = "",
    val listFoods: List<Food> = listOf(),
    val listCategories: List<String> = listOf(),
    val image: String = "",
    val rating: Double = 0.0,
    val ratingCount: Int = 0
) : Serializable