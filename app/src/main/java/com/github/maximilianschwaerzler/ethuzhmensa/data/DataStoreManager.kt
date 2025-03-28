package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.maximilianschwaerzler.ethuzhmensa.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("com.github.maximilianschwaerzler.ethuzhmensa.app_preferences")

class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {
    private val appDataStore = appContext.dataStore
    val lastMenuFetchDateKey = longPreferencesKey("last_menu_fetch_date")
    val lastFacilityFetchDateKey = longPreferencesKey("last_facility_fetch_date")
    val menuLanguageKey = stringPreferencesKey("menu_language")

    enum class MenuLanguage(val langCode: String) {
        GERMAN("de"), ENGLISH("en");

        fun getDisplayName(context: Context): String {
            return context.getString(
                when (this) {
                    GERMAN -> R.string.language_german
                    ENGLISH -> R.string.language_english
                }
            )
        }

        companion object {
            fun getFromLangCodeOrDefault(langCode: String): MenuLanguage {
                return entries.firstOrNull { it.langCode == langCode } ?: GERMAN
            }
        }
    }

    suspend fun updateMenuLanguage(menuLanguage: MenuLanguage) {
        appDataStore.edit { settings ->
            settings[menuLanguageKey] = menuLanguage.langCode
        }
    }

    val menuLanguage = appDataStore.data.map<Preferences, MenuLanguage> { preferences ->
        preferences[menuLanguageKey]?.let {
            MenuLanguage.getFromLangCodeOrDefault(it)
        } ?: MenuLanguage.GERMAN
    }

    suspend fun updateLastMenuFetchDate(date: LocalDate = LocalDate.now()) {
        appDataStore.edit { settings ->
            settings[lastMenuFetchDateKey] = date.toEpochDay()
        }
    }

    val lastMenuFetchDate = appDataStore.data.map<Preferences, LocalDate> { preferences ->
        preferences[lastMenuFetchDateKey]?.let {
            LocalDate.ofEpochDay(it)
        } ?: LocalDate.MIN
    }


    suspend fun updateLastFacilityFetchDate(date: LocalDate = LocalDate.now()) {
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