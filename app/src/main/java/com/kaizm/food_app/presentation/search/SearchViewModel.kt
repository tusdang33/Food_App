package com.kaizm.food_app.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.data.model.restaurant_data.Restaurant
import com.kaizm.food_app.domain.AuthRepository
import com.kaizm.food_app.domain.RestaurantRepository
import com.kaizm.food_app.domain.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val stateUI = _stateUI.asStateFlow()

    private val _search = MutableStateFlow<List<String>>(listOf())
    val search = _search.asStateFlow()
    var id = ""

    private var listTemp: MutableList<String> = mutableListOf()

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

    fun filter(data: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (data != "") {
                val tempListString = listTemp.filter { str ->
                    if (str.contains(data)) {
                        return@filter true
                    }
                    false
                }
                _search.emit(tempListString)
            }else{
                _search.emit(listTemp)
            }
        }
    }


    private fun getUserId() {
        viewModelScope.launch {
            authRepository.getCurrentUserId().fold(
                onSuccess = {
                    id = it
                }, onFailure = {
                }
            )

        }
    }

    fun addSearch(data: String) {
        viewModelScope.launch {
            searchRepository.postSearch(data, id).fold(onSuccess = {
                _event.trySend(Event.LoadDone)
            }, onFailure = { e ->
            })
        }
    }

    private fun loadSearch() {
        viewModelScope.launch {
            searchRepository.getSearch(id).collect { result ->
                result.fold(onSuccess = {
                    if (it != null) {
                        listTemp.addAll(it)
                    }
                    _event.send(Event.LoadDone)
                }, onFailure = {
                })
            }
        }
    }

    private fun loadRestaurant() {
        viewModelScope.launch {
            restaurantRepository.getRestaurant().collect { result ->
                result.fold(onSuccess = {
                    _stateUI.value = it
                    _event.send(Event.LoadDone)
                }, onFailure = {
                })
            }
        }
    }

    fun deleteSearch() {
        viewModelScope.launch {
            searchRepository.deleteSearch(id).fold(onSuccess = {
                _search.value = listOf()
            }, onFailure = {
            })
        }
    }
}