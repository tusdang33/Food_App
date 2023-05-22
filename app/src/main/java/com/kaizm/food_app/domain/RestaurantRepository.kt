package com.kaizm.food_app.domain


import com.kaizm.food_app.data.model.restaurant_data.Restaurant

import kotlinx.coroutines.flow.Flow


interface RestaurantRepository {

    suspend fun postRestaurant(restaurant: Restaurant): Result<Unit>
    suspend fun getRestaurant(): Flow<Result<List<Restaurant>>>
    suspend fun getCategory(): Result<List<String>>
    suspend fun deleteRestaurant(restaurant: Restaurant): Result<Unit>
    suspend fun updateRestaurant(restaurant: Restaurant): Result<Restaurant>
}