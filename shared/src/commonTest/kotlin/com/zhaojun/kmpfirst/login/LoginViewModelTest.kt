package com.zhaojun.kmpfirst.login

import com.zhaojun.kmpfirst.login.model.SessionState
import com.zhaojun.kmpfirst.login.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun restoreSession_whenNoUser_isUnauthenticated() = runTest(testDispatcher) {
        val viewModel = LoginViewModel(StubLoginRepository())
        advanceUntilIdle()

        assertEquals(SessionState.Unauthenticated, viewModel.uiState.value.session)
        assertFalse(viewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun restoreSession_whenUserExists_isAuthenticated() = runTest(testDispatcher) {
        val user = User(id = "1", username = "demo", displayName = "演示用户")
        val viewModel = LoginViewModel(StubLoginRepository(initialUser = user))
        advanceUntilIdle()

        assertEquals(SessionState.Authenticated(user), viewModel.uiState.value.session)
        assertEquals(user, viewModel.uiState.value.currentUser)
    }

    @Test
    fun login_withValidCredentials_updatesSession() = runTest(testDispatcher) {
        val viewModel = LoginViewModel(StubLoginRepository())
        advanceUntilIdle()

        viewModel.login("demo", "123456")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isLoggedIn)
        assertEquals("demo", state.currentUser?.username)
        assertFalse(state.isLoggingIn)
        assertNull(state.errorMessage)
    }

    @Test
    fun login_withInvalidCredentials_showsError() = runTest(testDispatcher) {
        val viewModel = LoginViewModel(StubLoginRepository())
        advanceUntilIdle()

        viewModel.login("wrong", "wrong")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoggedIn)
        assertEquals(SessionState.Unauthenticated, state.session)
        assertEquals("用户名或密码错误", state.errorMessage)
    }

    @Test
    fun logout_clearsSession() = runTest(testDispatcher) {
        val user = User(id = "1", username = "demo", displayName = "演示用户")
        val viewModel = LoginViewModel(StubLoginRepository(initialUser = user))
        advanceUntilIdle()

        viewModel.logout()
        advanceUntilIdle()

        assertEquals(SessionState.Unauthenticated, viewModel.uiState.value.session)
    }

    private class StubLoginRepository(
        initialUser: User? = null,
    ) : LoginRepository {
        private var currentUser: User? = initialUser

        override suspend fun getCurrentUser(): User? = currentUser

        override suspend fun login(username: String, password: String): Result<User> {
            val normalizedUsername = username.trim()
            if (normalizedUsername.isEmpty()) {
                return Result.failure(LoginException("请输入用户名"))
            }
            if (password.isEmpty()) {
                return Result.failure(LoginException("请输入密码"))
            }
            if (normalizedUsername != "demo" || password != "123456") {
                return Result.failure(LoginException("用户名或密码错误"))
            }
            val user = User(
                id = "demo-user",
                username = normalizedUsername,
                displayName = "演示用户",
            )
            currentUser = user
            return Result.success(user)
        }

        override suspend fun logout() {
            currentUser = null
        }
    }
}
