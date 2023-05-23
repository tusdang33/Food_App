package com.kaizm.food_app.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaizm.food_app.domain.SearchRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SearchRepositoryImpl : SearchRepository {
    private val searchCollectionRef = Firebase.firestore.collection("search")


    override suspend fun postSearch(data: String, uId: String): Result<Unit> {
        return try {
            val data = searchCollectionRef.document(uId).get().addOnSuccessListener {
                it.data?.let { map ->
                    if (map.isEmpty()) {
                        searchCollectionRef.document(uId).set(
                            hashMapOf(
                                "searches" to listOf(data)
                            )
                        )
                    } else {
                        searchCollectionRef.document(uId).update(
                            hashMapOf(
                                "searches" to FieldValue.arrayUnion(data)
                            ) as Map<String, Any>
                        )
                    }
                }
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSearch(uId: String): Flow<Result<List<String>?>> = callbackFlow {
        try {
            searchCollectionRef.document(uId).addSnapshotListener { value, error ->
                error?.let {
                    throw it
                }
                value?.let {
                    trySend(Result.success(it.get("searches") as List<String>?))
                }
            }
        } catch (e: Exception) {
            send(Result.failure(e))
        }
        awaitClose()
    }

    override suspend fun deleteSearch(uId: String): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any>(
                "searches" to FieldValue.delete(),
            )
            searchCollectionRef.document(uId).update(updates).addOnSuccessListener { }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}