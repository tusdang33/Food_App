package com.kaizm.food_app.domain

import com.kaizm.food_app.data.model.User

interface ProfileRepository {
    suspend fun addAccount(user: User)
}