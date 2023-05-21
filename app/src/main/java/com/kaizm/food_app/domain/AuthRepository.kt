package com.kaizm.food_app.domain

import com.kaizm.food_app.data.model.User

interface AuthRepository {
    suspend fun <T> checkCurrentUser(): T?
    suspend fun login(email: String, pass: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun register(email: String, pass: String): Result<User>
    suspend fun updatePass(pass: String): Result<Unit>
    suspend fun loadUser(): Result<User>
}