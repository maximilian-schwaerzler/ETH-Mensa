/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.viewmodel

import com.github.maximilianschwaerzler.ethuzhmensa.data.DataStoreManager.MenuLanguage

data class SettingsScreenUiState(
    val menuLanguage: MenuLanguage = MenuLanguage.GERMAN,
    val isLoading: Boolean = false,
)
