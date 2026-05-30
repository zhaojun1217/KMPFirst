package com.zhaojun.kmpfirst.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhaojun.kmpfirst.login.LoginViewModel

@Composable
fun LoginApp(
    viewModel: LoginViewModel = viewModel { LoginViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            uiState.isChecking -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.isLoggedIn -> {
                val user = uiState.currentUser
                if (user != null) {
                    HomeScreen(
                        user = user,
                        onLogout = viewModel::logout,
                    )
                }
            }

            else -> {
                LoginScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                )
            }
        }
    }
}
