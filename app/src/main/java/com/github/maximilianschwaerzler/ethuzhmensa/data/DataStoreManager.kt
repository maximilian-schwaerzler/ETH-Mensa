package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("com.github.maximilianschwaerzler.ethuzhmensa.data.app_preferences")

// @Singleton //You can ignore this annotation as return `datastore` from `preferencesDataStore` is singleton
class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {
    private val appDataStore = appContext.dataStore
    val KEY_LAST_MENU_SYNC_TIME = longPreferencesKey("last_menu_sync_time")
    val KEY_LAST_FACILITY_INFO_SYNC_TIME = longPreferencesKey("last_facility_info_sync_time")

    suspend fun setLastMenuSyncTime(time: Long) {
        appDataStore.edit { settings ->
            settings[KEY_LAST_MENU_SYNC_TIME] = time
        }
    }

    val lastMenuSyncTime = appDataStore.data.map { preferences ->
        preferences[KEY_LAST_MENU_SYNC_TIME] ?: 0
    }

    suspend fun setLastFacilitySyncTime(time: Long) {
        appDataStore.edit { settings ->
            settings[KEY_LAST_FACILITY_INFO_SYNC_TIME] = time
        }
    }

    val lastFacilitySyncTime = appDataStore.data.map { preferences ->
        preferences[KEY_LAST_FACILITY_INFO_SYNC_TIME] ?: 0
    }

//    suspend fun setThemeMode(mode: Int) {
//        settingsDataStore.edit { settings ->
//            settings[Settings.NIGHT_MODE] = mode
//        }
//    }
//
//    val themeMode: Flow<Int> = settingsDataStore.data.map { preferences ->
//        preferences[Settings.NIGHT_MODE] ?: AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
//    }
}