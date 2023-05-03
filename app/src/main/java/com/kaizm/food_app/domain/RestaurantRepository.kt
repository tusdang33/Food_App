package com.kaizm.food_app.domain

import android.net.Uri
import com.kaizm.food_app.data.model.Restaurants
import java.util.*

interface RestaurantRepository {

    suspend fun postRestaurant(restaurants: Restaurants): Result<Unit>
    suspend fun getCategory(): Result<List<String>>
}