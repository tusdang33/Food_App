package com.kaizm.food_app.presentation.add

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.Restaurants
import com.kaizm.food_app.domain.ImageRepository
import com.kaizm.food_app.domain.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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

    private suspend fun addRestaurantAndImage(name: String, uri: String) {
        restaurantRepository.postRestaurant(
            Restaurants("id", name, listOf(), listOf(), uri, 0.0)
        ).fold(
            onSuccess = {
                _event.trySend(Event.AddSuccess)
            }, onFailure = {
                Log.e(TAG, "addRestaurant: ${it.localizedMessage}")
                _event.trySend(Event.AddFail)
            }
        )
    }

    fun addRestaurantAndImage(name: String, uri: Uri?) {
        uri?.let {
            viewModelScope.launch {
                imageRepository.postImageRestaurant(uri).fold(
                    onSuccess = {
                        addRestaurantAndImage(name, it)
                    }, onFailure = {
                        Log.e(TAG, "addRestaurant: ${it.localizedMessage}")
                        _event.trySend(Event.AddFail)
                    }
                )
            }
        }
    }
}