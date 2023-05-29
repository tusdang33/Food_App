package com.kaizm.food_app.presentation.search_result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.data.model.restaurant_data.Restaurant
import com.kaizm.food_app.domain.RestaurantRepository
import com.kaizm.food_app.presentation.search.SearchViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(private val restaurantRepository: RestaurantRepository) :
    ViewModel() {
    private var listTemp: MutableList<Restaurant> = mutableListOf()


    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _stateUI = MutableStateFlow<List<Restaurant>>(listOf())
    val stateUI = _stateUI.asStateFlow()

    sealed class Event {
        object Loading : Event()
        object LoadDone : Event()
    }

    init {
        _event.trySend(Event.Loading)
        loadRestaurant()
    }

    fun filter(data: String) {
        listTemp = listTemp.filter { res ->
            res.listFoods.forEach {
                if (it.name.lowercase() == data.lowercase()){
                    return@filter true
                }
            }
            false
        }.toMutableList()
        _stateUI.value = listTemp
    }

    private fun loadRestaurant() {
        viewModelScope.launch {
            restaurantRepository.getRestaurant().collect { result ->
                result.fold(onSuccess = {
                    listTemp.addAll(it)
                    _event.send(Event.LoadDone)
                }, onFailure = {
                })
            }
        }
    }
}
