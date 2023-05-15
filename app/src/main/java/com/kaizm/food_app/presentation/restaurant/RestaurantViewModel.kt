package com.kaizm.food_app.presentation.restaurant

import androidx.lifecycle.ViewModel
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.data.model.restaurant_data.RestaurantDataItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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

        tempList.add(RestaurantDataItem("Title 1", dummyListFood()))
        tempList.add(RestaurantDataItem("Title 2", dummyListFood()))
        tempList.add(RestaurantDataItem("Title 3", dummyListFood()))
//        tempList.add(RestaurantDataItem("Title 4", dummyListFood()))
//        tempList.add(RestaurantDataItem("Title 5", dummyListFood()))
//        tempList.add(RestaurantDataItem("Title 6", dummyListFood()))
//        tempList.add(RestaurantDataItem("Title 7", dummyListFood()))
//        tempList.add(RestaurantDataItem("Title 8", dummyListFood()))
//        tempList.add(RestaurantDataItem("Title 9", dummyListFood()))
//        tempList.add(RestaurantDataItem("Title 10", dummyListFood()))

        return tempList
    }

    private fun dummyListFood(): List<Food> {
        val tempList = mutableListOf<Food>()
        for (i in 1..10) {
            tempList.add(Food("1", "Food $i", "Des $i", i + 1000L, listOf("Cat $i"), ""))
        }
        return tempList
    }
}