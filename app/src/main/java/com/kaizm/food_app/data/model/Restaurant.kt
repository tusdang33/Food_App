package com.kaizm.food_app.data.model

import java.io.Serializable

data class Restaurant(
    val id: String,
    val name: String,
    val rating: Double,
    val listFoods: List<Food>,
    val listCategories: List<String>,
    val image: String
) : Serializable