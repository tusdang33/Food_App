package com.kaizm.food_app.presentation.add_food

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.Food
import com.kaizm.food_app.domain.FoodRepository
import com.kaizm.food_app.domain.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository, private val imageRepository: ImageRepository
) : ViewModel() {

    sealed class Event {
        object AddSuccess : Event()
        data class AddFail(val message: String) : Event()
    }

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _listCategory = MutableStateFlow<List<String>>(listOf())
    val listCategory: StateFlow<List<String>>
        get() = _listCategory

    init {
        getDefaultFoodCategory()
    }

    fun addFood(
        resId: String, name: String, des: String, price: String, category: List<String>, uri: Uri?
    ) {
        if (name.isBlank() || des.isBlank() || price.isBlank() || category.isEmpty() || uri == null) {
            _event.trySend(Event.AddFail("Information Missing"))
        } else viewModelScope.launch {
            imageRepository.postImageRestaurant("food", uri).fold(onSuccess = {
                addFoodAndImage(
                    resId, Food(
                        System.currentTimeMillis().toString(),
                        name,
                        des,
                        price.toLong(),
                        category,
                        it
                    )
                )
            }, onFailure = {
                Log.e(TAG, "addRestaurant: ${it.localizedMessage}")
                _event.trySend(Event.AddFail(it.toString()))
            })
        }
    }

    private fun addFoodAndImage(resId: String, food: Food) {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.postFood(resId, food).fold(onSuccess = {
                Log.e(TAG, "addFood: Success")
                _event.send(Event.AddSuccess)
            }, onFailure = {
                _event.send(Event.AddFail(it.toString()))
                Log.e(TAG, "addFood: Fail")
            })
        }
    }


    private fun getDefaultFoodCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.getDefaultFoodCategory().collect { result ->
                result.fold(onSuccess = {
                    _listCategory.value = it
                    Log.e(TAG, "listCat: $it")
                }, onFailure = {
                    Log.e(TAG, "listCat: ${it.localizedMessage}")
                })
            }

            foodRepository.getListFood("4VY7rG960ekwHgyqFl62").collect { result ->
                result.fold(onSuccess = {
                    Log.e(TAG, "getListFoodViewModel: $it")
                }, onFailure = {
                    Log.e(TAG, "getListFoodViewModel: $it")
                })
            }
        }
    }
}