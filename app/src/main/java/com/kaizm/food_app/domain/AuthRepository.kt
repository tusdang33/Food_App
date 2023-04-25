package com.kaizm.food_app.domain

interface AuthRepository {
    suspend fun <T> checkCurrentUser(): T?
    suspend fun login(): Result<Boolean>
    suspend fun logout()
    suspend fun register(): Result<Boolean>
}