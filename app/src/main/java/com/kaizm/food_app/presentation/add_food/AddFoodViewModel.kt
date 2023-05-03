package com.kaizm.food_app.presentation.add_food

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.Food
import com.kaizm.food_app.domain.FoodRepository
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
    private val foodRepository: FoodRepository,
) : ViewModel() {

    sealed class Event() {
        object AddSuccess : Event()
        object AddFail : Event()
    }

    init {
        getDefaultFoodCategory()
    }

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _listCategory = MutableStateFlow<List<String>>(listOf())
    val listCategory: StateFlow<List<String>>
        get() = _listCategory

    fun addFood(name: String, des: String, price: Long, category: List<String>) {
        val food = Food(System.currentTimeMillis().toString(), name, des, price, category, "null")
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.postFood("4VY7rG960ekwHgyqFl62", food).fold(onSuccess = {
                Log.e(TAG, "addFood: Success")
                _event.send(Event.AddSuccess)
            }, onFailure = {
                _event.send(Event.AddFail)
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