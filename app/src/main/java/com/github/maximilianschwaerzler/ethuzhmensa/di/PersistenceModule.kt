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

    @Provides
    @Singleton
    fun provideFacilityDao(db: MensaDatabase): FacilityDao {
        return db.facilityDao()
    }

    @Provides
    @Singleton
    fun provideMenuDao(db: MensaDatabase): MenuDao {
        return db.menuDao()
    }
}