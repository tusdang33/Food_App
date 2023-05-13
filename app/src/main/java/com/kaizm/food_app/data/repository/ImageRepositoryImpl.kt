package com.kaizm.food_app.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kaizm.food_app.domain.ImageRepository
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class ImageRepositoryImpl(val context: Context) : ImageRepository {
    private val storageRef = Firebase.storage.reference

    @SuppressLint("Recycle")
    override suspend fun postImageRestaurant(path: String, uriImage: Uri): Result<String> {
        return try {
            //Put Image to Storage
            val childRef = storageRef.child(path + "/" + System.currentTimeMillis().toString())
            var downloadUrl = ""
            childRef.putBytes(compressImage(uriImage, 0.15)).await()
            childRef.downloadUrl.addOnSuccessListener {
                downloadUrl = it.toString()
            }.await()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun compressImage(uriImage: Uri, targetMB: Double): ByteArray {
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val tempFile = File.createTempFile("temp_image", ".jpg", storageDir)

        val imageFile = context.contentResolver.openInputStream(uriImage)?.let { inputStream ->
            val fileOutputStream = FileOutputStream(tempFile)
            inputStream.copyTo(fileOutputStream)
            inputStream.close()
            tempFile
        }

        val bitmap = BitmapFactory.decodeFile(imageFile?.absolutePath)
        val file = File(imageFile?.absolutePath.toString())
        val length = file.length()

        val fileSizeInMB = length.toDouble() / 1024 / 1024

        var quality = 100
        if (fileSizeInMB > targetMB) {
            quality = ((targetMB / fileSizeInMB) * 100).toInt()
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

        return byteArrayOutputStream.toByteArray()
    }
}