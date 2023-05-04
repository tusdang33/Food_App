package com.kaizm.food_app.domain

import com.kaizm.food_app.data.model.home_data.Banner
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    suspend fun getBanner(): Flow<Result<List<Banner>>>
}