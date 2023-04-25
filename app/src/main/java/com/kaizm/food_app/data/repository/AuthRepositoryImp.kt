package com.kaizm.food_app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.kaizm.food_app.domain.AuthRepository


class AuthRepositoryImp : AuthRepository{
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> checkCurrentUser(): T? {
        return FirebaseAuth.getInstance().currentUser as T
    }

    override suspend fun login(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun register(): Result<Boolean> {
        TODO("Not yet implemented")
    }
}