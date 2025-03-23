package com.github.maximilianschwaerzler.ethuzhmensa.di

import com.github.maximilianschwaerzler.ethuzhmensa.network.RequestInterceptor
import com.github.maximilianschwaerzler.ethuzhmensa.network.services.CookpitMenuService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CookpitRetrofitClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @CookpitRetrofitClient
    @Provides
    @Singleton
    fun provideRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(RequestInterceptor())
                    .build()
            )
            .baseUrl("https://idapps.ethz.ch")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCookpitMenuService(@CookpitRetrofitClient client: Retrofit): CookpitMenuService {
        return client.create(CookpitMenuService::class.java)
    }
}