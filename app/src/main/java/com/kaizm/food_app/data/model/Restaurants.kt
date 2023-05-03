package com.kaizm.food_app.data.model

import java.io.Serializable

data class Restaurants(
    var id: String = "0",
    val name: String,
    val listFoods: List<Foods>,
    val listCategories: List<String>,
    val image: String,
    var rating: Double
) : Serializable