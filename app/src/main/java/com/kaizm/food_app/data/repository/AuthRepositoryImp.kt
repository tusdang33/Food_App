package com.kaizm.food_app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.kaizm.food_app.data.model.User
import com.kaizm.food_app.domain.AuthRepository
import kotlinx.coroutines.tasks.await


class AuthRepositoryImp : AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> checkCurrentUser(): Result<T?> {
        return try {
            Result.success(firebaseAuth.currentUser as T)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, pass: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun register(email: String, pass: String): Result<User> {
        return try {
            val fireUser = firebaseAuth.createUserWithEmailAndPassword(email, pass).await().user!!
            Result.success(User(fireUser.uid, null, email, 0, null))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun updatePass(pass: String): Result<Unit> {
        return try {
            firebaseAuth.currentUser!!.updatePassword(pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(name: String, email: String): Result<Unit> {
        return try {
            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }
            firebaseAuth.currentUser!!.updateProfile(profileUpdates).await()
            firebaseAuth.currentUser!!.updateEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUid(): Result<String> {
        return try {
            Result.success(firebaseAuth.currentUser!!.uid)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}