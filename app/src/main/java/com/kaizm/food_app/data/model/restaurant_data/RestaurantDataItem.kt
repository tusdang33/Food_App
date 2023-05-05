package com.kaizm.food_app.data.model.restaurant_data

import com.kaizm.food_app.data.model.home_data.Title

class RestaurantDataItem(val viewType: Int) {
    var listCategory: List<String>? = null
    var title: Title? = null
    var listFood: List<Food>? = null
}