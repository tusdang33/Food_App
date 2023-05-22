package com.kaizm.food_app.data.model.order_data

import com.kaizm.food_app.data.model.restaurant_data.Food
import java.io.Serializable

data class FoodInOrder(
    val food: Food = Food(), var quantity: Int = 0
) : Serializable