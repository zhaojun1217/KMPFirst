package com.zhaojun.kmpfirst.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * 供 iOS SwiftUI 等原生 UI 调用的登录桥接层，内部复用 [LoginViewModel]。
 */
class LoginBridge {
    private val viewModel = LoginViewModel()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var observeJob: Job? = null

    fun startObserving(onUpdate: (LoginUiStateSnapshot) -> Unit) {
        observeJob?.cancel()
        observeJob = scope.launch {
            viewModel.uiState.collect { onUpdate(it.toSnapshot()) }
        }
    }

    fun login(username: String, password: String) {
        viewModel.login(username, password)
    }

    fun logout() {
        viewModel.logout()
    }

    fun clearError() {
        viewModel.clearError()
    }

    fun close() {
        observeJob?.cancel()
        scope.cancel()
    }
}
