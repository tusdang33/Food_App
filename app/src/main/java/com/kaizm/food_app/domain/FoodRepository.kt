package com.kaizm.food_app.domain

import com.kaizm.food_app.data.model.restaurant_data.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    suspend fun postFood(resId: String, food: Food): Result<Unit>
    suspend fun getDefaultFoodCategory(): Flow<Result<List<String>>>
    suspend fun getListFood(resId: String): Flow<Result<List<Food>?>>
    suspend fun deleteFood(resId: String, food: Food): Result<Unit>
    suspend fun updateFood(resId: String, oldFood: Food, newFood: Food): Result<Food>
}