package com.zhaojun.kmpfirst.login.model

sealed interface SessionState {
    data object Checking : SessionState

    data object Unauthenticated : SessionState

    data class Authenticated(val user: User) : SessionState
}

data class LoginUiState(
    val session: SessionState = SessionState.Checking,
    val isLoggingIn: Boolean = false,
    val errorMessage: String? = null,
) {
    val isChecking: Boolean
        get() = session is SessionState.Checking

    val isLoggedIn: Boolean
        get() = session is SessionState.Authenticated

    val currentUser: User?
        get() = (session as? SessionState.Authenticated)?.user
}
