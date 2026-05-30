package com.zhaojun.kmpfirst.login

import com.zhaojun.kmpfirst.login.model.LoginUiState

data class LoginUiStateSnapshot(
    val isChecking: Boolean = true,
    val isLoggedIn: Boolean = false,
    val isLoggingIn: Boolean = false,
    val errorMessage: String? = null,
    val displayName: String? = null,
    val username: String? = null,
)

fun LoginUiState.toSnapshot(): LoginUiStateSnapshot =
    LoginUiStateSnapshot(
        isChecking = isChecking,
        isLoggedIn = isLoggedIn,
        isLoggingIn = isLoggingIn,
        errorMessage = errorMessage,
        displayName = currentUser?.displayName,
        username = currentUser?.username,
    )
