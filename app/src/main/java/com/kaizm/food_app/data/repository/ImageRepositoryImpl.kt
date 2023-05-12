package com.kaizm.food_app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kaizm.food_app.domain.ImageRepository
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream


class ImageRepositoryImpl(val context: Context) : ImageRepository {
    private val storageRef = Firebase.storage.reference
    override suspend fun postImageRestaurant(path: String, uriImage: Uri): Result<String> {
        return try {
            //Downsize Image
            val inputStream = context.contentResolver.openInputStream(uriImage)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val scaleWidth: Int = bitmap.width / 4
            val scaleHeight: Int = bitmap.height / 4
            val downsizedImageBytes: ByteArray =
                getDownsizedImageBytes(bitmap, scaleWidth, scaleHeight)

            //Put Image to Storage
            val childRef = storageRef.child(path + "/" + System.currentTimeMillis().toString())
            var downloadUrl = ""
            childRef.putBytes(downsizedImageBytes).await()
            childRef.downloadUrl.addOnSuccessListener {
                downloadUrl = it.toString()
            }.await()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getDownsizedImageBytes(
        fullBitmap: Bitmap, scaleWidth: Int, scaleHeight: Int
    ): ByteArray {
        val scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, scaleWidth, scaleHeight, true)
        val byteOutput = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteOutput)
        return byteOutput.toByteArray()
    }
}