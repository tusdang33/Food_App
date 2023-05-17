package com.kaizm.food_app.domain

import com.kaizm.food_app.data.model.order_data.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun postOrder(order: Order): Result<Unit>
    suspend fun getOrder(
        uid: String?, resId: String?, orderState: Boolean
    ): Flow<Result<List<Order>>>

    suspend fun deleteOrder(orderId: String): Result<Order>
}