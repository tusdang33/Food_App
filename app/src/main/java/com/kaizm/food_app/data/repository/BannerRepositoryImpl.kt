package com.kaizm.food_app.data.repository

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.home_data.Banner
import com.kaizm.food_app.domain.BannerRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Suppress("UNCHECKED_CAST")
class BannerRepositoryImpl : BannerRepository {
    private val bannerCollectionRef = Firebase.firestore.collection("banner")
    override suspend fun getBanner(): Flow<Result<List<Banner>>> = callbackFlow {
        try {
            bannerCollectionRef.document("vTuHUjfCEDsJtNJ4dDEv").get().addOnSuccessListener {
//                trySend(Result.success())
                val map = it.get("banners") as List<Banner>

                Log.e(TAG, "getBanner: ${map.toList()} ")
            }.await()
        } catch (e: Exception) {
            send(Result.failure(e))
        }
        awaitClose()
    }
}
