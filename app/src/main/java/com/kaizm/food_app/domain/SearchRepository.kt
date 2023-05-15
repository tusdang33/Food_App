package com.kaizm.food_app.domain

import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    suspend fun postSearch(data: String, uId: String) : Result<Unit>
    suspend fun getSearch(uId: String): Flow<Result<List<String>>>
}