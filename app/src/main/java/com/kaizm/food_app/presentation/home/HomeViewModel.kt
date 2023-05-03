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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bannerRepository: BannerRepository
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
        viewModelScope.launch {
            fetchData()
        }
    }

    private suspend fun fetchData() {
        bannerRepository.getBanner()
            .onStart {
                _event.trySend(Event.Loading)
            }
            .collect { result ->
                Log.e(TAG, "fetchData: Run ?")

                result.fold(onSuccess = {
                    listBanner.addAll(it)
                }, onFailure = {
                    Log.e(TAG, "fetchHomeUI: ${it.localizedMessage}")
                })
                listTitle.add(Title(1, "Best"))
                listTitle.add(Title(2, "New"))
                listRestaurant.addAll(dummyRes())
                _event.send(Event.LoadDone)
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

    private fun dummyRes(): List<Restaurant> {
        val tempList = mutableListOf<Restaurant>()
        for (i in 1..12) {
            tempList.add(
                Restaurant(
                    "$i", "Name $i", listOf(), listOf(), "Image", 0.0
                )
            )
        }
        return tempList
    }
}