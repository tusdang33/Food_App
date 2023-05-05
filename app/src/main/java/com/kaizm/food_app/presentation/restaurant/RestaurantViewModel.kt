package com.kaizm.food_app.presentation.restaurant

import android.util.Log
import androidx.lifecycle.ViewModel
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.home_data.Title
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.data.model.restaurant_data.RestaurantDataItem
import com.kaizm.food_app.presentation.restaurant.RestaurantBodyAdapter.Companion.CATEGORY
import com.kaizm.food_app.presentation.restaurant.RestaurantBodyAdapter.Companion.FOOD
import com.kaizm.food_app.presentation.restaurant.RestaurantBodyAdapter.Companion.TITLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor() : ViewModel() {

    sealed class Event() {
        object Loading : Event()
        object LoadDone : Event()
        object GetSuccess : Event()
        data class GetFail(val message: String) : Event()
    }

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _listTopFood = MutableStateFlow<List<Food>>(listOf())
    val listTopFood: StateFlow<List<Food>>
        get() = _listTopFood

    private val _listBodyFood = MutableStateFlow<List<RestaurantDataItem>>(listOf())
    val listBodyFood: StateFlow<List<RestaurantDataItem>>
        get() = _listBodyFood

    init {
        _listTopFood.value = dummyListFood()
        _listBodyFood.value = dummyList()
    }

    private fun dummyList(): List<RestaurantDataItem> {
        val tempList = mutableListOf<RestaurantDataItem>()
        tempList.add(RestaurantDataItem(CATEGORY).apply {
            val tempCat = mutableListOf<String>()
            for (i in 1..6) {
                tempCat.add("Cate $i")
            }
            listCategory = tempCat
        })
        tempList.add(RestaurantDataItem(TITLE).apply {
            title = Title(1, "Title 1")
        })

        tempList.add(RestaurantDataItem(FOOD).apply {
            listFood = dummyListFood().subList(0, 3)
        })

        tempList.add(RestaurantDataItem(TITLE).apply {
            title = Title(1, "Title 2")
        })
        tempList.add(RestaurantDataItem(FOOD).apply {
            listFood = dummyListFood().subList(4, 6)
        })
        Log.e(TAG, "dummyList: $tempList", )
        return tempList
    }

    private fun dummyListFood(): List<Food> {
        val tempList = mutableListOf<Food>()
        for (i in 1..8) {
            tempList.add(Food("1", "Food $i", "Des $i", i + 1000L, listOf("Cat $i"), ""))
        }
        return tempList
    }
}