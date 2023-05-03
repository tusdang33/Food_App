package com.kaizm.food_app.data.model

import java.io.Serializable

data class Restaurant(
    var id: String,
    val name: String,
    val listFoods: List<Food>,
    val listCategories: List<String>,
    val image: String,
    val rating: Double,
) : Serializable