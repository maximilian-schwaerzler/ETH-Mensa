package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("com.github.maximilianschwaerzler.ethuzhmensa.app_preferences")

class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {
    private val appDataStore = appContext.dataStore
    val lastMenuFetchDateKey = longPreferencesKey("last_menu_fetch_date")
    val lastFacilityFetchDateKey = longPreferencesKey("last_facility_fetch_date")

    suspend fun setLastMenuFetchDate(date: LocalDate) {
        appDataStore.edit { settings ->
            settings[lastMenuFetchDateKey] = date.toEpochDay()
        }
    }

    val lastMenuFetchDate = appDataStore.data.map<Preferences, LocalDate> { preferences ->
        preferences[lastMenuFetchDateKey]?.let {
            LocalDate.ofEpochDay(it)
        } ?: LocalDate.MIN
    }


    suspend fun setLastFacilityFetchDate(date: LocalDate) {
        appDataStore.edit { settings ->
            settings[lastFacilityFetchDateKey] = date.toEpochDay()
        }
    }

    val lastFacilityFetchDate = appDataStore.data.map<Preferences, LocalDate> { preferences ->
        preferences[lastFacilityFetchDateKey]?.let {
            LocalDate.ofEpochDay(it)
        } ?: LocalDate.MIN
    }
}