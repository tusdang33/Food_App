package com.kaizm.food_app.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.kaizm.food_app.common.Const.TU
import com.kaizm.food_app.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class Event() {
        object LoginSuccess : Event()
        object LoginFail : Event()
    }

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()


    fun checkCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            if (authRepository.checkCurrentUser<FirebaseUser>() != null) {
                _event.send(Event.LoginSuccess)
            } else {
                _event.send(Event.LoginFail)

            }
        }
    }
}