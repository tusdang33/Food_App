package com.kaizm.food_app.data.di

import com.kaizm.food_app.data.repository.AuthRepositoryImp
import com.kaizm.food_app.data.repository.BannerRepositoryImpl
import com.kaizm.food_app.data.repository.ProfileRepositoryImpl
import com.kaizm.food_app.domain.AuthRepository
import com.kaizm.food_app.domain.BannerRepository
import com.kaizm.food_app.domain.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideBannerRepository(): BannerRepository {
        return BannerRepositoryImpl()
    }

}