package com.kaizm.food_app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.kaizm.food_app.data.model.User
import com.kaizm.food_app.domain.AuthRepository
import kotlinx.coroutines.tasks.await


class AuthRepositoryImp : AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> checkCurrentUser(): T? {
        return firebaseAuth.currentUser as T
    }

    override suspend fun login(email: String, pass: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun register(email: String, pass: String): Result<User> {
        return try {
            val fireUser = firebaseAuth.createUserWithEmailAndPassword(email, pass).await().user!!
            Result.success(User(fireUser.uid, null, email, 0, null))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUserId(): Result<String> {
        return try {
            Result.success(firebaseAuth.currentUser!!.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}