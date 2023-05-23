package com.kaizm.food_app.presentation.restaurant

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TU
import com.kaizm.food_app.data.model.order_data.FoodInOrder
import com.kaizm.food_app.data.model.order_data.Order
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.data.model.restaurant_data.RestaurantDataItem
import com.kaizm.food_app.domain.AuthRepository
import com.kaizm.food_app.domain.FoodRepository
import com.kaizm.food_app.domain.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)


data class RestaurantUiState(
    val isLoad: Boolean = true,
    val listTop: List<Food> = listOf(),
    val listBody: List<RestaurantDataItem> = listOf(),
    val listFoodInOrder: MutableSet<FoodInOrder> = mutableSetOf(),
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
        object Success : Event()
        object AddCartSuccess : Event()
        data class Error(val message: String) : Event()
    }

    var uId = ""
    var currentOrderId = ""
    var currentResId = ""

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _restaurantUiState = MutableStateFlow(RestaurantUiState())
    val restaurantUiState = _restaurantUiState.asStateFlow()

    init {
        getUid()
    }

    fun getFood(resId: String) {
        viewModelScope.launch {
            foodRepository.getListFood(resId)
                .onStart {
                    setLoading()
                }
                .collect { result ->
                    result.fold(onSuccess = { list ->
                        val tempMap = hashMapOf<String, MutableList<Food>>()
                        if (!list.isNullOrEmpty()) {
                            for (i in list) {
                                val currentList =
                                    tempMap.getOrDefault(
                                        i.category[0],
                                        mutableListOf()
                                    )
                                currentList.add(i)
                                tempMap[i.category[0]] = currentList
                            }

                            val tempList = mutableListOf<RestaurantDataItem>()
                            for ((key, value) in tempMap) {
                                tempList.add(RestaurantDataItem(key, value))
                            }
                            _restaurantUiState.update {
                                it.copy(
                                    isLoad = false,
                                    listTop = list.subList(0, 4),
                                    listBody = tempList
                                )
                            }
                        } else {
                            _restaurantUiState.update {
                                it.copy(
                                    isLoad = false,
                                    listTop = listOf(),
                                    listBody = listOf()
                                )
                            }
                        }

                    }, onFailure = {
                        _event.send(Event.Error(it.localizedMessage as String))
                    })
                }
        }
    }

    fun getOrder(resId: String) {
        viewModelScope.launch {
            orderRepository.getOrder(null, resId, true)
                .onStart { setLoading() }
                .collect { result ->
                    result.fold(onSuccess = { listOrder ->
                        if (listOrder.isNotEmpty()) {
                            _restaurantUiState.update {
                                currentOrderId = listOrder[0].id
                                it.copy(
                                    isLoad = false,
                                    listFoodInOrder = listOrder[0].listFood.toMutableSet(),
                                )
                            }
                            calPrice()
                        } else {
                            currentOrderId = ""

                        }
                    }, onFailure = {
                        _event.send(Event.Error(it.localizedMessage as String))
                    })
                }
        }
    }

    private fun deleteFoodInOrder(foodInOrder: FoodInOrder) {
        viewModelScope.launch {
            orderRepository.deleteFoodInOrder(currentOrderId, foodInOrder)
                .fold(onSuccess = {
                    setLoading()
                    _restaurantUiState.update {
                        _restaurantUiState.value.listFoodInOrder.remove(foodInOrder)
                        val tempList = mutableSetOf<FoodInOrder>()
                        tempList.addAll(_restaurantUiState.value.listFoodInOrder)
                        it.copy(
                            isLoad = false,
                            listFoodInOrder = tempList,
                        )
                    }
                    if (_restaurantUiState.value.listFoodInOrder.isEmpty()) {
                        deleteOrder()
                    }
                }, onFailure = {
                    _event.send(Event.Error(it.localizedMessage as String))
                })
        }
    }

    fun deleteOrder() {
        viewModelScope.launch {
            orderRepository.deleteOrder(currentOrderId)
                .fold(onSuccess = {
                    currentOrderId = ""
                    setLoading()
                    _restaurantUiState.update {
                        it.copy(
                            isLoad = false,
                            listFoodInOrder = mutableSetOf(),
                        )
                    }
                }, onFailure = {
                    _event.send(Event.Error(it.localizedMessage as String))
                })
        }
    }

    fun addFoodToOrder(food: Food) {
        setLoading()
        _restaurantUiState.update {
            val tempList = it.listFoodInOrder
            tempList.add(FoodInOrder(food, 1))
            it.copy(
                isLoad = false, listFoodInOrder = tempList
            )
        }
        _event.trySend(Event.AddCartSuccess)
        calPrice()
        postOrder(currentResId)
    }

    fun onPlusOrder(foodInOrder: FoodInOrder) {
        _restaurantUiState.update {
            it.listFoodInOrder.find { order ->
                order == foodInOrder
            }?.quantity = foodInOrder.quantity + 1
            it
        }
        calPrice()
    }

    fun onMinusOrder(foodInOrder: FoodInOrder) {
        if (foodInOrder.quantity > 1) {
            _restaurantUiState.update {
                it.listFoodInOrder.find { order ->
                    order == foodInOrder
                }?.quantity = foodInOrder.quantity - 1
                it
            }
        } else {
            deleteFoodInOrder(foodInOrder)
        }
        calPrice()
    }

    fun postOrder(resId: String) {
        viewModelScope.launch(NonCancellable) {
            if (_restaurantUiState.value.listFoodInOrder.isNotEmpty()) {
                orderRepository.postOrder(
                    Order(
                        currentOrderId,
                        uId,
                        resId,
                        _restaurantUiState.value.listFoodInOrder.toList(),
                        _restaurantUiState.value.totalPrice,
                        true
                    )
                )
                    .fold(onSuccess = {
                        Log.e(TU, "postOrder: $it")
                        currentOrderId = it.id
                    }, onFailure = {
                        _event.send(Event.Error(it.localizedMessage as String))
                    })
            }
        }
    }

    private fun calPrice() {
        var totalPrice = 0L
        _restaurantUiState.value.listFoodInOrder.forEach {
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
            authRepository.getCurrentUid()
                .fold(onSuccess = {
                    uId = it
                }, onFailure = {
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