package com.kaizm.food_app.domain

import android.net.Uri

interface ImageRepository {
    suspend fun postImageRestaurant(uriImage: Uri): Result<String>
}