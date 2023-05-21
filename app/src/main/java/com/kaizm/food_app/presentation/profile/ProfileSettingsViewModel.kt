package com.kaizm.food_app.presentation.profile

import androidx.lifecycle.ViewModel
import com.kaizm.food_app.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

data class UserUiState(
    val userName: String ="",
    val userEmail: String ="",
    val state: String = ""
)

@HiltViewModel
class ProfileSettingsViewModel@Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel()  {

    sealed class Event() {
        object Loading : Event()
        object LoadSuccess : Event()
    }

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _userUiState = MutableStateFlow(UserUiState())
    val userUiState = _userUiState.asStateFlow()



    init {
        _event.trySend(Event.Loading)
        loadData()
    }

    private fun loadData() {
        TODO("Not yet implemented")
    }






}