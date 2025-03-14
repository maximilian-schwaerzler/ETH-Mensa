package com.github.maximilianschwaerzler.ethuzhmensa.data.utils

import android.content.Context
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MensaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MensaDatabase {
        return MensaDatabase.getInstance(context)
    }
}