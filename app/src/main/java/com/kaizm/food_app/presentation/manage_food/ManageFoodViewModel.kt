package com.kaizm.food_app.presentation.manage_food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.data.model.Food
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

        data class GetFail(val message: String) : Event()
    }

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _listFood = MutableStateFlow<List<Food>>(listOf())
    val listFood: StateFlow<List<Food>>
        get() = _listFood


    init {
        getAllFood("8hIrdDZzn4JMA2CoKQvl")
    }

    fun getAllFood(resId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.getListFood(resId)
                .onStart {
                    _event.send(Event.Loading)
                }
                .collect { result ->
                result.fold(onSuccess = {
                    _listFood.value = it
                    _event.send(Event.GetSuccess)
                }, onFailure = {
                    _event.send(Event.GetFail(it.toString()))
                })
            }
        }
    }
}