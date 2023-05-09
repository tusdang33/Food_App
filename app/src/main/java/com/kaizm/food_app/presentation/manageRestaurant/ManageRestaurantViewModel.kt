package com.kaizm.food_app.presentation.manageRestaurant

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaizm.food_app.common.Const
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.data.model.home_data.HomeDataItem
import com.kaizm.food_app.domain.RestaurantRepository
import com.kaizm.food_app.presentation.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageRestaurantViewModel @Inject constructor(
    private val restaurantRepository : RestaurantRepository
) : ViewModel() {

    private val listRestaurant = mutableListOf<Restaurant>()

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
        viewModelScope.launch { restaurantRepository.getRestaurant().collect { result ->
            Log.e(Const.TAG, "fetchRes: Run ?")
            result.fold(onSuccess = {
                _stateUI.value = it
                _event.send(Event.LoadDone)
            }, onFailure = {
                Log.e(Const.TAG, "fetchRestaurant: ${it.localizedMessage}")
            })
        } }
    }
}