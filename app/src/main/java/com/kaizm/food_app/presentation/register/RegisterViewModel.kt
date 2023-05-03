package com.kaizm.food_app.presentation.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.domain.AuthRepository
import com.kaizm.food_app.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository, private val profileRepository: ProfileRepository
) : ViewModel() {

    sealed class Event() {
        object RegSuccess : Event()
        object RegFail : Event()
    }

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    fun register(email: String, pass: String, passConfirm: String) {
        if (email.isEmpty() && pass.isEmpty() && pass != passConfirm) {
            _event.trySend(Event.RegFail)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                authRepository.register(email, pass).fold(onSuccess = {
                    _event.trySend(Event.RegSuccess)
                    profileRepository.addAccount(it)
                }, onFailure = {
                    Log.e(TAG, "register: ${it.localizedMessage}")
                    _event.trySend(Event.RegFail)
                })
            }
        }
    }

}