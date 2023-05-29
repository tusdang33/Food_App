package com.kaizm.food_app.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.domain.FoodRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Suppress("UNCHECKED_CAST")
class FoodRepositoryImpl : FoodRepository {
    private val restaurantCollectionRef = Firebase.firestore.collection("restaurant")
    private val foodCategoryRef = Firebase.firestore.collection("category")
        .document("food")

    override suspend fun postFood(
        resId: String,
        food: Food
    ): Result<Unit> {
        return try {
            restaurantCollectionRef.document(resId)
                .update("listFoods", FieldValue.arrayUnion(food))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDefaultFoodCategory(): Flow<Result<List<String>>> =
        callbackFlow {
            try {
                foodCategoryRef.get()
                    .addOnSuccessListener {
                        trySend(Result.success(it.get("category") as List<String>))
                    }
                    .await()
            } catch (e: Exception) {
                send(Result.failure(e))
            }
            awaitClose()
        }


    override suspend fun getListFood(resId: String): Flow<Result<List<Food>?>> =
        callbackFlow {
            try {
                restaurantCollectionRef.get()
                restaurantCollectionRef.document(resId)
                    .addSnapshotListener { value, error ->
                        error?.let {
                            throw it
                        }
                        value?.let {
                            val foods =
                                it.get("listFoods") as ArrayList<HashMap<String, Any>>?
                            val foodList = foods?.map { map ->
                                mapToObject(map)
                            }
                            trySend(Result.success(foodList))

                        }
                    }
            } catch (e: Exception) {
                send(Result.failure(e))
            }
            awaitClose()
        }

    override suspend fun deleteFood(
        resId: String,
        food: Food
    ): Result<Unit> {
        return try {
            restaurantCollectionRef.document(resId)
                .update(
                    hashMapOf<String, Any>(
                        "listFoods" to FieldValue.arrayRemove(food)
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateFood(
        resId: String,
        oldFood: Food,
        newFood: Food
    ): Result<Food> {
        return try {
            Firebase.firestore.runBatch { batch ->
                batch.update(
                    restaurantCollectionRef.document(resId), hashMapOf<String, Any>(
                        "listFoods" to FieldValue.arrayRemove(oldFood)
                    )
                )
                batch.update(
                    restaurantCollectionRef.document(resId), hashMapOf<String, Any>(
                        "listFoods" to FieldValue.arrayUnion(newFood)
                    )
                )
            }.await()
            Result.success(newFood)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapToObject(hashMap: HashMap<String, Any>): Food {
        val id = hashMap["id"] as String
        val image = hashMap["image"] as String
        val description = hashMap["description"] as String
        val name = hashMap["name"] as String
        val price = hashMap["price"] as Long
        val category = hashMap["category"] as List<String>
        return Food(id, name, description, price, category, image)
    }
}