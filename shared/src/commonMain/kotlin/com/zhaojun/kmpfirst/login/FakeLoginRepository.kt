package com.zhaojun.kmpfirst.login

import com.zhaojun.kmpfirst.login.model.User
import kotlinx.coroutines.delay

/**
 * 本地演示用登录仓库。测试账号：用户名 `demo`，密码 `123456`。
 */
class FakeLoginRepository : LoginRepository {
    private var currentUser: User? = null

    override suspend fun getCurrentUser(): User? = currentUser

    override suspend fun login(username: String, password: String): Result<User> {
        val normalizedUsername = username.trim()
        if (normalizedUsername.isEmpty()) {
            return Result.failure(LoginException("请输入用户名"))
        }
        if (password.isEmpty()) {
            return Result.failure(LoginException("请输入密码"))
        }

        delay(500)

        if (normalizedUsername != DEMO_USERNAME || password != DEMO_PASSWORD) {
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

    private companion object {
        const val DEMO_USERNAME = "demo"
        const val DEMO_PASSWORD = "123456"
    }
}

class LoginException(message: String) : Exception(message)
