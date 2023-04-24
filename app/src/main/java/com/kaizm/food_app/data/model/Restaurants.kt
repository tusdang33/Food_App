package com.kaizm.food_app.data.model

import java.io.Serializable

data class Restaurants(
    val id: String,
    val name: String,
    val listFoods: List<Foods>,
    val listCategories: List<String>,
    val image: String
) : Serializable