package com.kaizm.food_app.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const
import com.kaizm.food_app.data.model.home_data.Banner
import com.kaizm.food_app.data.model.home_data.HomeDataItem
import com.kaizm.food_app.data.model.home_data.Title
import com.kaizm.food_app.data.model.restaurant_data.Restaurant
import com.kaizm.food_app.domain.BannerRepository
import com.kaizm.food_app.domain.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bannerRepository: BannerRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val listBanner = mutableListOf<Banner>()
    private val listTitle = mutableListOf<Title>()
    private val listRestaurant = mutableListOf<Restaurant>()

    sealed class Event {
        object Loading : Event()
        object LoadDone : Event()
    }

    data class FetchState(
        val banner: Boolean = false,
        val restaurant: Boolean = false
    )

    var dataFlag = MutableStateFlow(FetchState())

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _stateUI = MutableStateFlow<List<HomeDataItem>>(listOf())
    val stateUI: StateFlow<List<HomeDataItem>> = _stateUI

    init {
        _event.trySend(Event.Loading)
        viewModelScope.launch {
            dataFlag.collect { flag ->
                if (flag.banner && flag.restaurant) {
                    _event.send(Event.LoadDone)
                }
            }
        }
        fetchData()
    }

    private fun fetchData() {
        listTitle.add(Title(1, "Featured Partner"))
        listTitle.add(Title(2, "All Restaurant"))
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                bannerRepository.getBanner()
                    .collect { result ->
                        result.fold(onSuccess = {
                            listBanner.addAll(it)
                            dataFlag.update { state ->
                                state.copy(
                                    banner = true
                                )
                            }
                        }, onFailure = {})
                    }
            }

            launch {
                restaurantRepository.getRestaurant()
                    .collect { result ->
                        result.fold(onSuccess = {
                            listRestaurant.addAll(it)
                            dataFlag.update { state ->
                                state.copy(
                                    restaurant = true
                                )
                            }
                        }, onFailure = {})
                    }
            }
        }
    }

    fun fetchHomeUI() {
        val tempList = mutableListOf<HomeDataItem>()
        tempList.add(HomeDataItem(HomeAdapter.TYPE_BANNER).apply {
            banner = listBanner[0]
        })
        tempList.add(HomeDataItem(HomeAdapter.TYPE_TITLE).apply {
            title = listTitle[0]
        })
        tempList.add(HomeDataItem(HomeAdapter.TYPE_FEATURED).apply {
            listRestaurant = this@HomeViewModel.listRestaurant.reversed().subList(0,6)
        })
        tempList.add(HomeDataItem(HomeAdapter.TYPE_BANNER).apply {
            banner = listBanner[1]
        })
        tempList.add(HomeDataItem(HomeAdapter.TYPE_TITLE).apply {
            title = listTitle[1]
        })
        tempList.add(HomeDataItem(HomeAdapter.TYPE_ALL).apply {
            listRestaurant = this@HomeViewModel.listRestaurant.subList(6, 12)
        })
        _stateUI.value = tempList
    }
}