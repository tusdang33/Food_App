package com.kaizm.food_app.presentation.manage_food

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TU
import com.kaizm.food_app.data.model.Food
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.domain.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    sealed class Event() {
        object Loading : Event()
        object LoadDone : Event()
        object GetSuccess : Event()
        object GetNull : Event()
        data class GetFail(val message: String) : Event()
    }

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _listFood = MutableStateFlow<List<Food>>(listOf())
    val listFood: StateFlow<List<Food>>
        get() = _listFood

    fun getAllFood(resId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.getListFood(resId)
                .onStart {
                    _event.send(Event.Loading)
                }
                .collect { result ->
                    result.fold(onSuccess = { list ->
                        if (list != null && list.isNotEmpty()) {
                            _listFood.value = list
                        } else {
                            _event.send(Event.GetNull)
                        }
                        _event.send(Event.LoadDone)
                    }, onFailure = {
                        _event.send(Event.GetFail(it.toString()))
                    })
                }
        }
    }

    fun delete(
        restaurant: Restaurant,
        food: Food
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.deleteFood(restaurant.id, food)
                .fold(onSuccess = {
                    Log.e(TU, "delete: Done")
                }, onFailure = {
                    Log.e(TU, "delete: Fail")
                })
        }
    }
}