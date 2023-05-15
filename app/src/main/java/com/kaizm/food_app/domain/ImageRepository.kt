package com.kaizm.food_app.domain

import android.net.Uri

interface ImageRepository {
    suspend fun postImageRestaurant(path: String, uriImage: Uri): Result<String>
}