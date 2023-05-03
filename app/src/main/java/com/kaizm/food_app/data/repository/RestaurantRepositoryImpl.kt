package com.kaizm.food_app.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaizm.food_app.data.model.Restaurants
import com.kaizm.food_app.domain.RestaurantRepository
import kotlinx.coroutines.tasks.await

class RestaurantRepositoryImpl : RestaurantRepository {
    private val restaurantCollectionRef = Firebase.firestore.collection("restaurant")


    override suspend fun postRestaurant(restaurants: Restaurants): Result<Unit> {
        val fireId = restaurantCollectionRef.document().id
        restaurants.id = fireId
        return try {
            restaurantCollectionRef.document(fireId).set(restaurants).await()
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure<Unit>(e)
        }
    }


}