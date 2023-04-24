package com.kaizm.food_app.data.model

import java.io.Serializable

data class Foods(
    val id: String,
    val name: String,
    val category: String,
    val image: String
) : Serializable