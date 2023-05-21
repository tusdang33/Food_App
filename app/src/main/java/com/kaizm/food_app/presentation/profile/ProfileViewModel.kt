package com.kaizm.food_app.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const
import com.kaizm.food_app.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    sealed class Event() {
        object LogoutSuccess : Event()
        object LogoutFail : Event()
    }

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.logout().fold(onSuccess = {
                _event.trySend(Event.LogoutSuccess)
            }, onFailure = {
                Log.e(Const.TAG, "register: ${it.localizedMessage}")
                _event.trySend(Event.LogoutFail)
            })
        }
    }
}