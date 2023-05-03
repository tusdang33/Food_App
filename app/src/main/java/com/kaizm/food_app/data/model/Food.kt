package com.kaizm.food_app.data.model

import java.io.Serializable

data class Food(
    val id: String,
    val name: String,
    val description: String,
    val price: Long,
    val category: List<String>,
    val image: String
) : Serializable