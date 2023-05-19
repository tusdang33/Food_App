package com.kaizm.food_app.data.model.restaurant_data

import java.io.Serializable

data class Restaurant(
    var id: String = "",
    val name: String = "",
    val listFoods: List<Food> = listOf(),
    val listCategories: List<String> = listOf(),
    val image: String = "",
    val rating: Double = 0.0
) : Serializable