package com.zhaojun.kmpfirst.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhaojun.kmpfirst.login.model.LoginUiState
import com.zhaojun.kmpfirst.login.model.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository = FakeLoginRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        restoreSession()
    }

    fun login(username: String, password: String) {
        if (_uiState.value.isLoggingIn) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoggingIn = true,
                    errorMessage = null,
                )
            }

            loginRepository.login(username, password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            session = SessionState.Authenticated(user),
                            isLoggingIn = false,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            session = SessionState.Unauthenticated,
                            isLoggingIn = false,
                            errorMessage = error.message ?: "登录失败，请稍后重试",
                        )
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            loginRepository.logout()
            _uiState.update {
                LoginUiState(session = SessionState.Unauthenticated)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun restoreSession() {
        viewModelScope.launch {
            val user = loginRepository.getCurrentUser()
            _uiState.update {
                it.copy(
                    session = if (user != null) {
                        SessionState.Authenticated(user)
                    } else {
                        SessionState.Unauthenticated
                    },
                )
            }
        }
    }
}
