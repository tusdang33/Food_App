package com.kaizm.food_app.data.model.home_data

import com.kaizm.food_app.data.model.Restaurant

class HomeDataItem(val viewType: Int) {
    var banner: Banner? = null
    var title: Title? = null
    var listRestaurant: List<Restaurant>? = null
}