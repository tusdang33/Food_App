package com.kaizm.food_app.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.data.model.home_data.Banner
import com.kaizm.food_app.data.model.home_data.HomeDataItem
import com.kaizm.food_app.data.model.home_data.Title
import com.kaizm.food_app.domain.BannerRepository
import com.kaizm.food_app.domain.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _stateUI = MutableStateFlow<List<HomeDataItem>>(listOf())
    val stateUI: StateFlow<List<HomeDataItem>>
        get() = _stateUI

    init {

        fetchData()
    }

    private fun fetchData() {
        listTitle.add(Title(1, "Best"))
        listTitle.add(Title(2, "New"))
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                bannerRepository.getBanner().collect { result ->
                    Log.e(TAG, "fetchBanner: Run ?")
                    result.fold(onSuccess = {
                        listBanner.addAll(it)
                    }, onFailure = {
                        Log.e(TAG, "fetchBanner: ${it.localizedMessage}")
                    })
                }
            }
            launch {
                restaurantRepository.getRestaurant().collect { result ->
                    Log.e(TAG, "fetchRes: Run ?")
                    result.fold(onSuccess = {
                        listRestaurant.addAll(it)
                        _event.send(Event.LoadDone)
                    }, onFailure = {
                        Log.e(TAG, "fetchRestaurant: ${it.localizedMessage}")
                    })
                }
            }
        }
    }

    fun fetchHomeUI() {
        val tempList = mutableListOf<HomeDataItem>()
        tempList.add(HomeDataItem(HomeAdapter.TYPE_BANNER).apply { banner = listBanner[0] })
        tempList.add(HomeDataItem(HomeAdapter.TYPE_TITLE).apply { title = listTitle[0] })
        tempList.add(HomeDataItem(HomeAdapter.TYPE_BEST).apply {
            listRestaurant = this@HomeViewModel.listRestaurant.subList(0, 6)
        })
        tempList.add(HomeDataItem(HomeAdapter.TYPE_BANNER).apply { banner = listBanner[1] })
        tempList.add(HomeDataItem(HomeAdapter.TYPE_TITLE).apply { title = listTitle[1] })
        tempList.add(HomeDataItem(HomeAdapter.TYPE_NEWEST).apply {
            listRestaurant = this@HomeViewModel.listRestaurant.subList(6, 12)
        })
        _stateUI.value = tempList
    }
}