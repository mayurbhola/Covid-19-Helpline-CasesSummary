package com.example.covid_19_helpline.ui.main

import android.os.Handler
import androidx.core.os.postDelayed
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var state: LoginViewState = LoginViewState.Idle

    private val _stateLiveData = MutableLiveData<LoginViewState>(LoginViewState.Idle)

    val stateLiveData: LiveData<LoginViewState> = _stateLiveData

    fun onSubmit(email: String, password: String) {
        when {
            isEmailInvalid(email) -> _stateLiveData.value =
                LoginViewState.Failed(message = "Email is Invalid")

            isPasswordInvalid(password) -> _stateLiveData.value =
                LoginViewState.Succeed(message = "Password is Invalid")

            else -> {
                _stateLiveData.value = LoginViewState.Progress
                processLogin { hasSucceed ->
                    if (hasSucceed) {
                        _stateLiveData.value =
                            LoginViewState.Succeed(message = "Yay Login Success!!")
                    } else {
                        _stateLiveData.value = LoginViewState.Failed(message = "Login has Failed!")
                    }
                }
            }
        }
    }

    private fun processLogin(callback: (Boolean) -> Unit) {
        Handler().postDelayed(3000) {
            callback(true)
        }
    }

    private fun isPasswordInvalid(password: String): Boolean {
        return password.count() < 4
    }

    private fun isEmailInvalid(email: String): Boolean {
        return email.singleOrNull { it == '@' } == null
    }
}

sealed class LoginViewState {
    object Idle : LoginViewState()

    object Progress : LoginViewState()

    data class Failed(val message: String?) : LoginViewState()

    data class Succeed(val message: String?) : LoginViewState()
}