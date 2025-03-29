/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.di

import android.content.Context
import android.net.ConnectivityManager
import com.github.maximilianschwaerzler.ethuzhmensa.network.RequestInterceptor
import com.github.maximilianschwaerzler.ethuzhmensa.network.services.CookpitFacilityService
import com.github.maximilianschwaerzler.ethuzhmensa.network.services.CookpitMenuService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class CookpitRetrofitClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @CookpitRetrofitClient
    @Provides
    @Singleton
    fun provideCookpitRetrofitClient(@ApplicationContext appContext: Context): Retrofit {
        return Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(RequestInterceptor(appContext))
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

    @Provides
    @Singleton
    fun provideCookpitFacilityService(@CookpitRetrofitClient client: Retrofit): CookpitFacilityService {
        return client.create(CookpitFacilityService::class.java)
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(ConnectivityManager::class.java)
}