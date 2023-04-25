package com.kaizm.food_app.data.repository

import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaizm.food_app.data.model.User
import com.kaizm.food_app.domain.ProfileRepository
import java.lang.reflect.Method

class ProfileRepositoryImpl:ProfileRepository {
    private val accountCollectionRef = Firebase.firestore.collection("account")

    override suspend fun addAccount(user: User) {
        accountCollectionRef.document(user.id).set(user, SetOptions.merge())
    }
}