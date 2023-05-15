package com.kaizm.food_app.domain

import com.kaizm.food_app.data.model.User

interface AuthRepository {
    suspend fun <T> checkCurrentUser(): T?
    suspend fun login(email: String, pass: String): Result<Unit>
    suspend fun logout()
    suspend fun register(email: String, pass: String): Result<User>
    suspend fun getCurrentUserId(): Result<String>
}