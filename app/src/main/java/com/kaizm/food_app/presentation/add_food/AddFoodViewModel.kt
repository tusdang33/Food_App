package com.kaizm.food_app.presentation.add_food


import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TU
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.domain.FoodRepository
import com.kaizm.food_app.domain.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    sealed class Event {
        object AddSuccess : Event()
        data class AddFail(val message: String) : Event()
    }

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _listCategory = MutableStateFlow<List<String>>(listOf())
    val listCategory = _listCategory.asStateFlow()

    init {
        getDefaultFoodCategory()
    }

    fun addFood(
        resId: String,
        name: String,
        des: String,
        price: String,
        category: List<String>,
        uri: Uri? = null,
        oldFood: Food? = null
    ) {
        if (oldFood == null) {
            if (name.isBlank() || des.isBlank() || price.isBlank() || category.isEmpty() || uri == null) {
                _event.trySend(Event.AddFail("Information Missing in Add"))
            } else viewModelScope.launch {
                Log.e(TU, "addFood: Add")
                imageRepository.postImageRestaurant("food", uri)
                    .fold(onSuccess = {
                        addFoodAndImage(
                            resId, Food(
                                System.currentTimeMillis()
                                    .toString(),
                                name,
                                des,
                                price.toLong(),
                                category,
                                it
                            )
                        )
                    }, onFailure = {
                        Log.e(TU, "addRestaurant: ${it.localizedMessage}")
                        _event.trySend(Event.AddFail(it.toString()))
                    })
            }
        } else {
            if (name.isBlank() || des.isBlank() || price.isBlank() || category.isEmpty()) {
                _event.trySend(Event.AddFail("Information Missing in Update"))
            } else viewModelScope.launch {
                if (uri != null) {
                    imageRepository.postImageRestaurant("food", uri)
                        .fold(onSuccess = {
                            updateFoodAndImage(
                                resId, oldFood, Food(
                                    oldFood.id,
                                    name,
                                    des,
                                    price.toLong(),
                                    category,
                                    it
                                )
                            )
                        }, onFailure = {
                            Log.e(TU, "addRestaurant: ${it.localizedMessage}")
                            _event.trySend(Event.AddFail(it.toString()))
                        })
                } else {
                    Log.e(TU, "addFood: $resId without img")
                    updateFoodAndImage(
                        resId, oldFood, Food(
                            oldFood.id,
                            name,
                            des,
                            price.toLong(),
                            category,
                            oldFood.image
                        )
                    )
                }
            }


        }
    }

    private suspend fun addFoodAndImage(
        resId: String,
        food: Food
    ) {
        foodRepository.postFood(resId, food)
            .fold(onSuccess = {
                Log.e(TU, "addFood: Success")
                _event.send(Event.AddSuccess)
            }, onFailure = {
                _event.send(Event.AddFail(it.toString()))
                Log.e(TU, "addFood: Fail")
            })
    }

    private suspend fun updateFoodAndImage(
        resId: String,
        oldFood: Food,
        newFood: Food
    ) {
        foodRepository.updateFood(resId, oldFood, newFood)
            .fold(onSuccess = {
                Log.e(TU, "addFood: Success")
                _event.send(Event.AddSuccess)
            }, onFailure = {
                _event.send(Event.AddFail(it.toString()))
                Log.e(TU, "addFood: Fail ${it.localizedMessage}")
            })
    }


    private fun getDefaultFoodCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.getDefaultFoodCategory()
                .collect { result ->
                    result.fold(onSuccess = {
                        _listCategory.value = it
                        Log.e("AAA", "listCat: $it")
                    }, onFailure = {
                        Log.e("AAA", "listCat: ${it.localizedMessage}")
                    })
                }
        }
    }
}