package com.kaizm.food_app.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizm.food_app.common.Const.TU
import com.kaizm.food_app.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class Event() {
        object UpdateSuccess : Event()
        data class UpdateFail(val message: String) : Event()
    }

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    fun updatePassword(pass: String, newPass: String, confirmPass: String) {
        if (pass == newPass || newPass != confirmPass) {
            _event.trySend(Event.UpdateFail("Fail on check valid"))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                authRepository.updatePass(newPass).fold(onSuccess = {
                    _event.trySend(Event.UpdateSuccess)
                }, onFailure = {
                    Log.e(TU, "register: ${it.localizedMessage}")
                    _event.trySend(Event.UpdateFail("Update Fail on API"))
                })
            }
        }
    }
}