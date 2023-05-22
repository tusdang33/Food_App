package com.kaizm.food_app.presentation.add_restaurant

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TU
import com.kaizm.food_app.data.model.restaurant_data.Restaurant
import com.kaizm.food_app.domain.ImageRepository
import com.kaizm.food_app.domain.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        data class AddFail(val message: String) : Event()
    }

    private val _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _listCategory = MutableStateFlow<List<String>>(listOf())
    val listCategory = _listCategory.asStateFlow()

    init {
        categoryRestaurant()
    }

    private suspend fun addRestaurantAndImage(restaurant: Restaurant) {
        restaurantRepository.postRestaurant(restaurant)
            .fold(onSuccess = {
                _event.trySend(Event.AddSuccess)
            }, onFailure = {
                Log.e(TU, "addRestaurant: ${it.localizedMessage}")
                _event.trySend(Event.AddFail(it.toString()))
            })
    }

    fun addRestaurant(
        name: String,
        uri: Uri? = null,
        listCategory: List<String>,
        oldRestaurant: Restaurant? = null,
    ) {
        if (oldRestaurant == null) {
            if (name.isBlank() || uri == null || listCategory.isEmpty()) {
                _event.trySend(Event.AddFail("Information Missing"))
            } else viewModelScope.launch {
                imageRepository.postImageRestaurant("restaurant", uri)
                    .fold(onSuccess = {
                        addRestaurantAndImage(
                            Restaurant(
                                id = "id",
                                name = name,
                                listFoods = listOf(),
                                listCategories = listCategory,
                                image = it,
                                rating = 0.0
                            )
                        )
                    }, onFailure = {
                        Log.e(TU, "addRestaurant: ${it.localizedMessage}")
                        _event.trySend(Event.AddFail(it.toString()))
                    })
            }
        } else {
            if (name.isBlank() || listCategory.isEmpty()) {
                _event.trySend(Event.AddFail("Information Missing"))
            } else viewModelScope.launch {
                if (uri == null) {
                    update(
                        Restaurant(
                            id = oldRestaurant.id,
                            name = name,
                            listFoods = oldRestaurant.listFoods,
                            listCategories = listCategory,
                            image = oldRestaurant.image,
                            rating = 0.0
                        )
                    )
                } else {
                    imageRepository.postImageRestaurant("restaurant", uri)
                        .fold(onSuccess = { uri ->
                            update(
                                Restaurant(
                                    id = oldRestaurant.id,
                                    name = name,
                                    listFoods = oldRestaurant.listFoods,
                                    listCategories = listCategory,
                                    image = uri,
                                    rating = 0.0
                                )
                            )
                        }, onFailure = {
                            Log.e(TU, "addRestaurant: ${it.localizedMessage}")
                            _event.trySend(Event.AddFail(it.toString()))
                        })
                }
            }
        }

    }


    private fun categoryRestaurant() {
        viewModelScope.launch {
            restaurantRepository.getCategory()
                .fold(onSuccess = {
                    _listCategory.value = it
                }, onFailure = {
                    Log.e("AAA", "categoryRestaurant: ${it.localizedMessage}")
                })
        }
    }

    fun update(restaurant: Restaurant) {
        viewModelScope.launch {
            restaurantRepository.updateRestaurant(restaurant)
                .fold(onSuccess = {
                    _event.send(Event.AddSuccess)
                }, onFailure = {
                    _event.send(Event.AddFail("Update Fail"))
                })
        }
    }
}