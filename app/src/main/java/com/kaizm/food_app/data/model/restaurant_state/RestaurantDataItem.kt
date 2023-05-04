package com.kaizm.food_app.data.model.restaurant_state

import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.data.model.home_data.Title

class RestaurantDataItem(val viewType: Int) {
    var title: Title? = null
    var listCategory: List<String>? = null
    var listRestaurant: List<Restaurant>? = null
}