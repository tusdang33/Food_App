package com.kaizm.food_app.data.repository

import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

import com.kaizm.food_app.data.model.restaurant_data.Restaurant

import com.kaizm.food_app.domain.RestaurantRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RestaurantRepositoryImpl : RestaurantRepository {
    private val restaurantCollectionRef = Firebase.firestore.collection("restaurant")
    private val categoryCollectionRef = Firebase.firestore.collection("category")
        .document("food")


    override suspend fun postRestaurant(restaurant: Restaurant): Result<Unit> {
        val fireId = restaurantCollectionRef.document().id
        restaurant.id = fireId
        return try {
            restaurantCollectionRef.document(fireId)
                .set(restaurant)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRestaurant(): Flow<Result<List<Restaurant>>> =
        callbackFlow {
            try {
                restaurantCollectionRef.addSnapshotListener { value, error ->
                    error?.let {
                        throw it
                    }
                    value?.let { snapShots ->
                        val listRestaurant = mutableListOf<Restaurant>()
                        for (snapShot in snapShots.documents) {
                            snapShot.toObject<Restaurant>()
                                ?.let {
                                    listRestaurant.add(it)
                                }
                        }
                        trySend(Result.success(listRestaurant))
                    }
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
            categoryCollectionRef.get()
                .addOnSuccessListener {
                    list.addAll(it.get("category") as List<String>)
                }
                .await()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteRestaurant(restaurant: Restaurant): Result<Unit> {
        return try {
            restaurantCollectionRef.document(restaurant.id)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRestaurant(restaurant: Restaurant): Result<Restaurant> {
        return try {
            restaurantCollectionRef.document(restaurant.id)
                .set(
                    restaurant,
                    SetOptions.merge()
                )
                .await()
            Result.success(restaurant)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRestaurantById(listResId: List<String>): Result<List<Restaurant>> {
        return try {
            val tempList = mutableListOf<Restaurant>()
            Firebase.firestore.runTransaction { transaction ->
                listResId.forEach {
                    tempList.add(
                        transaction.get(restaurantCollectionRef.document(it))
                            .toObject<Restaurant>()!!
                    )
                }
            }.await()

            Result.success(tempList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}