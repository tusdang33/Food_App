package com.kaizm.food_app.presentation.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TU
import com.kaizm.food_app.data.model.order_data.Order
import com.kaizm.food_app.data.model.order_data.OrderWithRes
import com.kaizm.food_app.domain.AuthRepository
import com.kaizm.food_app.domain.OrderRepository
import com.kaizm.food_app.domain.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {
    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _tempedOrderList =
        MutableStateFlow<List<OrderWithRes>>(listOf())
    val tempedOrderList = _tempedOrderList.asStateFlow()

    private val _orderedList = MutableStateFlow<List<OrderWithRes>>(listOf())
    val orderedList = _orderedList.asStateFlow()

    sealed class Event {
        object Loading : Event()
        object LoadDone : Event()
        object Success : Event()
        data class Fail(val message: String) : Event()
    }

    init {
        getUid()
    }

    private fun getOrder(
        uid: String,
    ) {
        viewModelScope.launch {
            orderRepository.getOrder(uid = uid, orderState = true)
                .collect { result ->
                    result.fold(onSuccess = { listOrder ->
                        if (listOrder.isNotEmpty()) {
                            mappingList(listOrder)
                            _event.send(Event.Loading)
                        }
                    }, onFailure = {
                        _event.send(Event.Fail(it.localizedMessage as String))
                    })
                }
        }
    }

    private fun mappingList(listOrder: List<Order>) {
        viewModelScope.launch {
            val tempSet = mutableSetOf<String>()
            listOrder.forEach {
                tempSet.add(it.resId)
            }
            getResById(tempSet.toList(), listOrder)
        }
    }

    private fun getResById(
        listResId: List<String>,
        listOrder: List<Order>
    ) {
        viewModelScope.launch {
            restaurantRepository.getRestaurantById(listResId)
                .fold(
                    onSuccess = { listRes ->
                        val tempOrderList = mutableListOf<OrderWithRes>()
                        val orderedList = mutableListOf<OrderWithRes>()
                        listOrder.forEach { order ->
                            Log.e(TU, "getResById: $order", )
                            listRes.forEach { res ->
                                if (res.id == order.resId && order.tempOrder) {
                                    tempOrderList.add(OrderWithRes(order, res))
                                } else {
                                    orderedList.add(OrderWithRes(order, res))
                                }
                            }
                        }
                        _orderedList.value = orderedList
                        _tempedOrderList.value = tempOrderList
                        _event.send(Event.LoadDone)
                    }, onFailure = {
                        Log.e(TU, "getResById: Fail $it")
                    }
                )
        }
    }

    private fun getUid() {
        viewModelScope.launch {
            authRepository.getCurrentUid()
                .fold(onSuccess = {
                    getOrder(it)
                }, onFailure = {
                })
        }
    }
}