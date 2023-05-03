package com.kaizm.food_app.presentation.add

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.domain.ImageRepository
import com.kaizm.food_app.domain.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddRestaurantViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    sealed class Event {
        object AddSuccess : Event()
        object AddFail : Event()
    }

    private val _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()
    private val _listCategory = MutableStateFlow<List<String>>(listOf())
    val listCategory: StateFlow<List<String>>
        get() = _listCategory

    init {
        categoryRestaurant()
    }

    private suspend fun addRestaurantAndImage(name: String, uri: String, list: List<String>) {
        restaurantRepository.postRestaurant(
            Restaurant("id", name, listOf(), list, uri, 0.0)
        ).fold(onSuccess = {
            _event.trySend(Event.AddSuccess)
        }, onFailure = {
            Log.e(TAG, "addRestaurant: ${it.localizedMessage}")
            _event.trySend(Event.AddFail)
        })
    }

    fun addRestaurantAndImage(name: String, uri: Uri?, list: List<String>) {
        uri?.let {
            viewModelScope.launch {
                imageRepository.postImageRestaurant(uri).fold(onSuccess = {
                    addRestaurantAndImage(name, it, list)
                }, onFailure = {
                    Log.e(TAG, "addRestaurant: ${it.localizedMessage}")
                    _event.trySend(Event.AddFail)
                })
            }
        }
    }

    private fun categoryRestaurant() {
        viewModelScope.launch {
            restaurantRepository.getCategory().fold(onSuccess = {
                _listCategory.value = it
            }, onFailure = {
                Log.e(TAG, "categoryRestaurant: ${it.localizedMessage}")
            })
        }
    }
}