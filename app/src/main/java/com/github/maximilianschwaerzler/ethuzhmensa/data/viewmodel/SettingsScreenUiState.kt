package com.github.maximilianschwaerzler.ethuzhmensa.data.viewmodel

import com.github.maximilianschwaerzler.ethuzhmensa.data.DataStoreManager.MenuLanguage

data class SettingsScreenUiState(
    val menuLanguage: MenuLanguage = MenuLanguage.GERMAN,
    val isLoading: Boolean = false,
)
