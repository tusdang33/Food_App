package com.kaizm.food_app.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.Food
import com.kaizm.food_app.domain.FoodRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Suppress("UNCHECKED_CAST")
class FoodRepositoryImpl : FoodRepository {
    private val restaurantCollectionRef = Firebase.firestore.collection("restaurant")
    private val foodCategoryRef = Firebase.firestore.collection("category").document("food")
    override suspend fun postFood(resId: String, food: Food): Result<Unit> {
        return try {
            restaurantCollectionRef.document(resId).update("listFoods", FieldValue.arrayUnion(food))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDefaultFoodCategory(): Flow<Result<List<String>>> = callbackFlow {
        try {
            foodCategoryRef.get().addOnSuccessListener {
                trySend(Result.success(it.get("category") as List<String>))
            }.await()
        } catch (e: Exception) {
            send(Result.failure(e))
        }
        awaitClose()
    }

    override suspend fun getListFood(resId: String): Flow<Result<List<Food>>> = callbackFlow {
        try {
            restaurantCollectionRef.document(resId).addSnapshotListener { value, error ->
                error?.let {
                    throw it
                }
                value?.let {
                    trySend(Result.success(it.get("listFoods") as List<Food>))
                }
            }
        } catch (e: Exception) {
            send(Result.failure(e))
        }
        awaitClose()
    }
}