package com.kaizm.food_app.data.di

import com.kaizm.food_app.data.repository.*
import com.kaizm.food_app.domain.*
import android.content.Context
import com.kaizm.food_app.data.repository.*
import com.kaizm.food_app.domain.*
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
    fun provideOrderRepository(): OrderRepository {
        return OrderRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideImageRepository(@ApplicationContext context: Context): ImageRepository {
        return ImageRepositoryImpl(context)

    }

    @Singleton
    @Provides
    fun provideBannerRepository(): BannerRepository {
        return BannerRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideSearchRepository(): SearchRepository {
        return SearchRepositoryImpl()
    }

}