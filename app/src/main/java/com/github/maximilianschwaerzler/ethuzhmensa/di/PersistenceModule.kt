/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.di

import android.content.Context
import androidx.room.Room
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.FacilityDao
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MensaDatabase
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MenuDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module for providing the database and DAOs using DI.
 */
@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    /**
     * Provides the Room database instance.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MensaDatabase {
        return Room
            .databaseBuilder(
                appContext, MensaDatabase::class.java,
                appContext.getString(R.string.db_name)
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Provides the Facility DAO.
     */
    @Provides
    @Singleton
    fun provideFacilityDao(db: MensaDatabase): FacilityDao {
        return db.facilityDao()
    }

    /**
     * Provides the Menu DAO.
     */
    @Provides
    @Singleton
    fun provideMenuDao(db: MensaDatabase): MenuDao {
        return db.menuDao()
    }
}