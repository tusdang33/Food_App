package com.kaizm.food_app.data.model.restaurant_data

import java.io.Serializable

data class Food(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Long = 0L,
    val category: List<String> = listOf(),
    val image: String = ""
) : Serializable