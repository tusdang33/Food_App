package com.kaizm.food_app.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.domain.AuthRepository
import com.kaizm.food_app.domain.RestaurantRepository
import com.kaizm.food_app.domain.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val searchRepository: SearchRepository,
    private val authRepository: AuthRepository
) :
    ViewModel() {

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()


    private val _stateUI = MutableStateFlow<List<Restaurant>>(listOf())
    val stateUI: StateFlow<List<Restaurant>>
        get() = _stateUI

    private val _search = MutableStateFlow<List<String>>(listOf())
    val search: StateFlow<List<String>>
        get() = _search
    var id = ""

    sealed class Event {
        object Loading : Event()
        object LoadDone : Event()
        data class AddFail(val message: String) : SearchViewModel.Event()
    }

    init {
        getUserId()
        _event.trySend(Event.Loading)
        loadRestaurant()
        loadSearch()
    }

    private fun getUserId() {
        viewModelScope.launch {
            authRepository.getCurrentUserId().fold(
                onSuccess = {
                    id = it
                }, onFailure = {
                    Log.e(TAG, "addSearch: fail")
                }
            )

        }
    }

    fun addSearch(data: String) {
        viewModelScope.launch {
            searchRepository.postSearch(data, id).fold(onSuccess = {
                _event.trySend(Event.LoadDone)
                Log.e(TAG, "addSearch: suc")
            }, onFailure = { e ->
                Log.e(TAG, "addSearch: ${e.localizedMessage}")
            })
        }
    }


    private fun loadSearch() {
        viewModelScope.launch {
            searchRepository.getSearch(id).collect { result ->
                result.fold(onSuccess = {
                    _search.value = it
                    Log.e(TAG, "loadSearch: $it", )
                    _event.send(Event.LoadDone)
                }, onFailure = {
                    Log.e(Const.TAG, "fetchSearch: ${it.localizedMessage}")
                })
            }
        }
    }

    private fun loadRestaurant() {
        viewModelScope.launch {
            restaurantRepository.getRestaurant().collect { result ->
                Log.e(Const.TAG, "fetchRes: Run ?")
                result.fold(onSuccess = {
                    _stateUI.value = it
                    _event.send(Event.LoadDone)
                }, onFailure = {
                    Log.e(Const.TAG, "fetchRestaurant: ${it.localizedMessage}")
                })
            }
        }
    }
}