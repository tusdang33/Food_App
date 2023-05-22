package com.kaizm.food_app.data.model.order_data

import com.kaizm.food_app.data.model.restaurant_data.Restaurant

data class OrderWithRes(
    val order: Order,
    val restaurant: Restaurant
)