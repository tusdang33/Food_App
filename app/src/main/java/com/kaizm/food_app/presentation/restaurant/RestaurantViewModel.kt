package com.kaizm.food_app.presentation.restaurant

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.order_data.FoodInOrder
import com.kaizm.food_app.data.model.order_data.Order
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.data.model.restaurant_data.RestaurantDataItem
import com.kaizm.food_app.domain.AuthRepository
import com.kaizm.food_app.domain.FoodRepository
import com.kaizm.food_app.domain.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)


data class RestaurantUiState(
    val isLoad: Boolean = true,
    val listTop: List<Food> = listOf(),
    val listBody: List<RestaurantDataItem> = listOf(),
    val listOrder: MutableSet<FoodInOrder> = mutableSetOf(),
    val totalPrice: Int = 0,
    val state: String = ""
)

@RequiresApi(Build.VERSION_CODES.N)
@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class Event {
        object GetSuccess : Event()
        object AddCartSuccess : Event()
        data class GetFail(val message: String) : Event()
    }

    private var uId = ""
    private var currentOrderId = ""

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _restaurantUiState = MutableStateFlow(RestaurantUiState())
    val restaurantUiState = _restaurantUiState.asStateFlow()

    init {
        getFood("97PS0oLeElLtWZgezSOK")
        getUid()
        getOrder("97PS0oLeElLtWZgezSOK")
    }


    private fun getFood(resId: String) {
        viewModelScope.launch {
            foodRepository.getListFood(resId).onStart {
                setLoading()
            }.collect { result ->
                result.fold(onSuccess = { list ->
                    val tempMap = hashMapOf<String, MutableList<Food>>()
                    for (i in list) {
                        val currentList = tempMap.getOrDefault(i.category[0], mutableListOf())
                        currentList.add(i)
                        tempMap[i.category[0]] = currentList
                    }

                    val tempList = mutableListOf<RestaurantDataItem>()
                    for ((key, value) in tempMap) {
                        tempList.add(RestaurantDataItem(key, value))
                    }
                    _restaurantUiState.update {
                        it.copy(
                            isLoad = false, listTop = list.subList(0, 4), listBody = tempList
                        )
                    }
                }, onFailure = {

                })
            }
        }
    }

    private fun getOrder(resId: String) {
        viewModelScope.launch {
            orderRepository.getOrder(null, resId, true).onStart { setLoading() }.collect { result ->
                result.fold(onSuccess = { listOrder ->
                    if (listOrder.isNotEmpty()) {
                        _restaurantUiState.update {
                            currentOrderId = listOrder[0].id
                            it.copy(
                                isLoad = false,
                                listOrder = listOrder[0].listFood.toMutableSet(),
                            )
                        }
                        calPrice()
                    }
                }, onFailure = {

                })
            }
        }
    }

    fun addToOrder(food: Food) {
        setLoading()
        _restaurantUiState.update {
            val tempList = it.listOrder
            tempList.add(FoodInOrder(food, 1))
            it.copy(
                isLoad = false, listOrder = tempList
            )
        }
        _event.trySend(Event.AddCartSuccess)
        calPrice()

        viewModelScope.launch {
            if (_restaurantUiState.value.listOrder.isNotEmpty()) {
                orderRepository.postOrder(
                    Order(
                        currentOrderId,
                        uId,
                        "97PS0oLeElLtWZgezSOK",
                        _restaurantUiState.value.listOrder.toList(),
                        _restaurantUiState.value.totalPrice,
                        true
                    )
                )
            } else {
                orderRepository.postOrder(
                    Order(
                        "",
                        uId,
                        "97PS0oLeElLtWZgezSOK",
                        _restaurantUiState.value.listOrder.toList(),
                        _restaurantUiState.value.totalPrice,
                        true
                    )
                )
            }
        }
    }

    fun onPlusOrder(foodInOrder: FoodInOrder) {
        _restaurantUiState.update {
            it.listOrder.find { order ->
                order == foodInOrder
            }?.quantity = foodInOrder.quantity + 1
            it
        }
        calPrice()
    }

    fun onMinusOrder(foodInOrder: FoodInOrder) {
        _restaurantUiState.update {
            it.listOrder.find { order ->
                order == foodInOrder
            }?.quantity = foodInOrder.quantity - 1
            it
        }
        calPrice()
    }

    private fun calPrice() {
        var totalPrice = 0L
        _restaurantUiState.value.listOrder.forEach {
            totalPrice += it.food.price * it.quantity
        }
        _restaurantUiState.update {
            it.copy(
                totalPrice = totalPrice.toInt()
            )
        }
    }

    private fun getUid() {
        viewModelScope.launch {
            authRepository.getCurrentUid().fold(onSuccess = {
                uId = it
            }, onFailure = {
                Log.e(TAG, "getUid: $it")
            })
        }
    }

    private fun setLoading() {
        _restaurantUiState.update {
            it.copy(
                isLoad = true
            )
        }
    }
}