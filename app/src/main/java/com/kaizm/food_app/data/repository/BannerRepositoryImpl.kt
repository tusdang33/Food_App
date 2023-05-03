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


class BannerRepositoryImpl : BannerRepository {
    private val bannerCollectionRef = Firebase.firestore.collection("banner")

    @Suppress("UNCHECKED_CAST")
    override suspend fun getBanner(): Flow<Result<List<Banner>>> = callbackFlow {
        try {
            bannerCollectionRef.document("vTuHUjfCEDsJtNJ4dDEv").get().addOnSuccessListener {
                val banners = it.get("banners") as ArrayList<HashMap<String, Any>>
                val bannerList = banners.map { map ->
                    mapToObject(map)
                }
                trySend(Result.success(bannerList))
            }.await()
        } catch (e: Exception) {
            send(Result.failure(e))
        }
        awaitClose()
    }

    private fun mapToObject(hashMap: HashMap<String, Any>): Banner {
        val id = hashMap["id"] as Long
        val name = hashMap["img"] as String
        return Banner(id.toInt(), name)
    }
}
