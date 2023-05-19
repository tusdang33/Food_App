package com.kaizm.food_app.data.di

import android.content.Context
import com.kaizm.food_app.data.repository.AuthRepositoryImp
import com.kaizm.food_app.data.repository.BannerRepositoryImpl
import com.kaizm.food_app.data.repository.FoodRepositoryImpl
import com.kaizm.food_app.data.repository.ImageRepositoryImpl
import com.kaizm.food_app.data.repository.ProfileRepositoryImpl
import com.kaizm.food_app.data.repository.RestaurantRepositoryImpl
import com.kaizm.food_app.domain.AuthRepository
import com.kaizm.food_app.domain.BannerRepository
import com.kaizm.food_app.domain.FoodRepository
import com.kaizm.food_app.domain.ImageRepository
import com.kaizm.food_app.domain.ProfileRepository
import com.kaizm.food_app.domain.RestaurantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImp()
    }

    @Singleton
    @Provides
    fun provideProfileRepository(): ProfileRepository {
        return ProfileRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideFoodRepository(): FoodRepository {
        return FoodRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideRestaurantRepository(): RestaurantRepository {
        return RestaurantRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideImageRepository(@ApplicationContext context: Context): ImageRepository{
        return ImageRepositoryImpl(context)
    }

    @Singleton
    @Provides
    fun provideBannerRepository(): BannerRepository {
        return BannerRepositoryImpl()
    }

}