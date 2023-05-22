package com.kaizm.food_app.presentation.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.data.model.order_data.FoodInOrder
import com.kaizm.food_app.data.model.order_data.Order
import com.kaizm.food_app.domain.AuthRepository
import com.kaizm.food_app.domain.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val isLoading: Boolean = true,
    val listFoodInOrder: List<FoodInOrder> = listOf(),
    val subTotal: Int = 0,
    val total: Int = 0
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uId = ""
    var currentOrderId = ""
    var currentResId = ""

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _checkoutStateUi = MutableStateFlow(CheckoutUiState())
    val checkoutStateUi = _checkoutStateUi.asStateFlow()

    sealed class Event {
        object Loading : Event()
        object LoadDone : Event()
        object Success : Event()
        data class Fail(val message: String) : Event()
    }

    init {
        getUid()
    }

    fun getFoodInOder(resId: String) {
        viewModelScope.launch {
            orderRepository.getOrder(null, resId, true)
                .onStart { setLoading() }
                .collect { result ->
                    result.fold(onSuccess = { listOrder ->
                        if (listOrder.isNotEmpty()) {
                            currentOrderId = listOrder[0].id
                            _checkoutStateUi.update {
                                it.copy(
                                    isLoading = false,
                                    listFoodInOrder = listOrder[0].listFood,
                                    subTotal = listOrder[0].totalPrice,
                                    total = listOrder[0].totalPrice,
                                )
                            }
                        }
                    }, onFailure = {
                        _event.send(Event.Fail(it.localizedMessage as String))
                    })
                }
        }
    }

    fun checkoutOrder(
        resId: String,
        tempOrder: Order? = null
    ) {
        viewModelScope.launch(NonCancellable) {
            orderRepository.postOrder(
                tempOrder?.copy(
                    tempOrder = false
                )
                    ?: Order(
                        currentOrderId,
                        uId,
                        resId,
                        _checkoutStateUi.value.listFoodInOrder.toList(),
                        _checkoutStateUi.value.total,
                        false
                    )
            )
                .fold(onSuccess = {
                    currentOrderId = it.id
                }, onFailure = {
                    _event.send(Event.Fail(it.localizedMessage as String))
                })
        }
    }

    private fun setLoading() {
        _checkoutStateUi.update {
            it.copy(
                isLoading = true
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

}