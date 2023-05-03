package com.kaizm.food_app.domain

import com.kaizm.food_app.data.model.Restaurant
import java.util.*

interface RestaurantRepository {

    suspend fun postRestaurant(restaurant: Restaurant): Result<Unit>
    suspend fun getCategory(): Result<List<String>>
}