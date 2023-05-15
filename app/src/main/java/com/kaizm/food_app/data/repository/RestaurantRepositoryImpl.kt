package com.kaizm.food_app.data.repository

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.domain.RestaurantRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
            Result.failure(e)
        }
    }

    override suspend fun getRestaurant(): Flow<Result<List<Restaurant>>> = callbackFlow {
        try {
            restaurantCollectionRef.get().addOnSuccessListener { snapShots ->
                val listRestaurant = mutableListOf<Restaurant>()
                for (snapShot in snapShots.documents) {
                    snapShot.toObject<Restaurant>()?.let {
                        listRestaurant.add(it)
                    }
                }
                trySend(Result.success(listRestaurant))
            }
        } catch (e: Exception) {
            send(Result.failure(e))
        }
        awaitClose()
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