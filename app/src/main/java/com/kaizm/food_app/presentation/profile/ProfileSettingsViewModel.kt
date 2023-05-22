package com.kaizm.food_app.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.kaizm.food_app.domain.AuthRepository
import com.kaizm.food_app.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val userEmail: String = "",
    val state: String = ""
)

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class Event {
        object Loading : Event()
        object LoadSuccess : Event()
        data class LoadFail(val message: String) : Event()
    }

    private var _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val _userUiState = MutableStateFlow(UserUiState())
    val userUiState = _userUiState.asStateFlow()


    init {
        _event.trySend(Event.Loading)
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.checkCurrentUser<FirebaseUser>().fold(
                onSuccess = { fireUser ->
                    fireUser?.let { user ->
                        setLoading()
                        _userUiState.update {
                            it.copy(
                                isLoading = false,
                                userEmail = user.email.toString(),
                                userName = user.displayName.toString()
                            )
                        }
                    }
                }, onFailure = {
                    _event.trySend(Event.LoadFail("Get User"))
                }
            )

        }
    }

    fun updateUser(name: String, email: String) {
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            authRepository.updateProfile(name, email).fold(onSuccess = {
                _event.trySend(Event.LoadSuccess)
            }, onFailure = {
                _event.trySend(Event.LoadFail(it.message.toString()))
            })
        }
    }

    private fun setLoading() {
        _userUiState.update {
            it.copy(
                isLoading = true
            )
        }
    }
}