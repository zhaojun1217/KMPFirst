package com.zhaojun.kmpfirst.login

import com.zhaojun.kmpfirst.login.model.User

interface LoginRepository {
    suspend fun getCurrentUser(): User?

    suspend fun login(username: String, password: String): Result<User>

    suspend fun logout()
}
