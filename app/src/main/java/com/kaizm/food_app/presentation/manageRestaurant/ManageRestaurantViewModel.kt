package com.kaizm.food_app.presentation.manageRestaurant

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const
import com.kaizm.food_app.data.model.restaurant_data.Restaurant
import com.kaizm.food_app.domain.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageRestaurantViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _stateUI = MutableStateFlow<List<Restaurant>>(listOf())
    val stateUI: StateFlow<List<Restaurant>>
        get() = _stateUI

    sealed class Event {
        object Loading : Event()
        object LoadDone : Event()
    }

    init {
        _event.trySend(Event.Loading)
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            restaurantRepository.getRestaurant()
                .collect { result ->
                    result.fold(onSuccess = {
                        _stateUI.value = it
                        _event.send(Event.LoadDone)
                    }, onFailure = {
                        Log.e(Const.TU, "fetchRestaurant: ${it.localizedMessage}")
                    })
                }
        }
    }
}