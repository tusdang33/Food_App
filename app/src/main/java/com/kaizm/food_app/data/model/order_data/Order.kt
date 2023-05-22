package com.kaizm.food_app.data.model.order_data

data class Order(
    var id: String = "",
    val uid: String = "",
    val resId: String = "",
    val listFood: List<FoodInOrder> = listOf(),
    val totalPrice: Int = 0,
    val isTempOrder: Boolean = false,
)