/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.DataStoreManager
import com.github.maximilianschwaerzler.ethuzhmensa.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val menuRepository: MenuRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dataStoreManager.menuLanguage
                .distinctUntilChanged()
                .collect { menuLanguage ->
                    _uiState.value = _uiState.value.copy(menuLanguage = menuLanguage)
                }
        }
    }

    fun updateMenuLanguage(menuLanguage: DataStoreManager.MenuLanguage) {
        Toast.makeText(
            appContext,
            appContext.getString(
                R.string.updating_menu_language,
                menuLanguage.getDisplayName(appContext)
            ),
            Toast.LENGTH_SHORT
        ).show()
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            if (menuRepository.tryRebuildDatabase(menuLanguage).isSuccess) {
                dataStoreManager.updateMenuLanguage(menuLanguage)
            } else {
                _uiState.value = _uiState.value.copy(
                    event = SettingsScreenUiState.UiEvent.ShowSnackbar(
                        "No internet connection. Please try again later."
                    )
                )
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}