package com.kaizm.food_app.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kaizm.food_app.data.model.order_data.FoodInOrder
import com.kaizm.food_app.data.model.order_data.Order
import com.kaizm.food_app.domain.OrderRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OrderRepositoryImpl : OrderRepository {
    private val orderCollectionRef = Firebase.firestore.collection("order")

    override suspend fun postOrder(order: Order): Result<Order> {
        return try {
            if (order.id != "") {
                orderCollectionRef.document(order.id)
                    .set(order, SetOptions.merge())
                    .await()
            } else {
                val fireId = orderCollectionRef.document().id
                order.id = fireId
                orderCollectionRef.document(fireId)
                    .set(order)
                    .await()
            }
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrder(
        uid: String?,
        resId: String?,
        orderState: Boolean
    ): Flow<Result<List<Order>>> = callbackFlow {
        try {
            if (uid != null) {
                orderCollectionRef.whereEqualTo("uid", uid)
                    .get()
                    .addOnSuccessListener {
                        val listOrder = mutableListOf<Order>()
                        for (document in it.documents) {
                            document.toObject<Order>()
                                ?.let { order ->
                                    listOrder.add(order)
                                }
                        }
                        trySend(Result.success(listOrder))
                    }
            } else {
                orderCollectionRef.whereEqualTo("resId", resId)
                    .whereEqualTo("tempOrder", orderState)
                    .get()
                    .addOnSuccessListener {
                        val listOrder = mutableListOf<Order>()
                        for (document in it.documents) {
                            document.toObject<Order>()
                                ?.let { order ->
                                    listOrder.add(order)
                                }
                        }
                        trySend(Result.success(listOrder))
                    }
            }
        } catch (e: Exception) {
            send(Result.failure(e))
        }
        awaitClose()
    }

    override suspend fun deleteFoodInOrder(
        orderId: String,
        foodInOrder: FoodInOrder
    ): Result<FoodInOrder> {
        return try {
            orderCollectionRef.document(orderId)
                .update(
                    hashMapOf<String, Any>(
                        "listFood" to FieldValue.arrayRemove(foodInOrder)
                    )
                )
                .await()
            Result.success(foodInOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteOrder(orderId: String): Result<Unit> {
        return try {
            orderCollectionRef.document(orderId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}