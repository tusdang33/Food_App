package com.kaizm.food_app.data.model

import android.provider.ContactsContract
import java.io.Serializable

data class User(
    val id: String,
    val name: String?,
    val email: String,
    val role: Int,
    val image: String?
) : Serializable