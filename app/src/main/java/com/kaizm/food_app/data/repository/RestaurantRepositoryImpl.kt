package com.kaizm.food_app.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.domain.RestaurantRepository
import kotlinx.coroutines.tasks.await

class RestaurantRepositoryImpl : RestaurantRepository {
    private val restaurantCollectionRef = Firebase.firestore.collection("restaurant")
    private val categoryCollectionRef = Firebase.firestore.collection("category").document("food")


    override suspend fun postRestaurant(restaurant: Restaurant): Result<Unit> {
        val fireId = restaurantCollectionRef.document().id
        restaurant.id = fireId
        return try {
            restaurantCollectionRef.document(fireId).set(restaurant).await()
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure<Unit>(e)
        }
    }

    override suspend fun getCategory(): Result<List<String>> {
        val list = mutableListOf<String>()

        return try {
            categoryCollectionRef.get().addOnSuccessListener {
                list.addAll(it.get("category") as List<String>)
            }.await()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}