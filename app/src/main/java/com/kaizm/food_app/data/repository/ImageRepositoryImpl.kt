package com.kaizm.food_app.data.repository

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kaizm.food_app.domain.ImageRepository
import kotlinx.coroutines.tasks.await

class ImageRepositoryImpl : ImageRepository {
    private val storageRef = Firebase.storage.reference
    override suspend fun postImageRestaurant(uriImage: Uri): Result<String> {
        return try {
            val childRef = storageRef.child(System.currentTimeMillis().toString())
            var downloadUrl: String = ""
            childRef.putFile(uriImage).await()
            childRef.downloadUrl.addOnSuccessListener {
                downloadUrl = it.toString()
            }.await()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}