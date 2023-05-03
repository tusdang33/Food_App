package com.kaizm.food_app.data.model

import java.io.Serializable

data class Food(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val image: String
) : Serializable