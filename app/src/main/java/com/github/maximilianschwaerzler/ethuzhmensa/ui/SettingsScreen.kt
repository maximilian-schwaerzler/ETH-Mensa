/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.DataStoreManager.MenuLanguage
import com.github.maximilianschwaerzler.ethuzhmensa.data.viewmodel.SettingsScreenUiState
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    menuLanguage: MenuLanguage,
    isLoading: Boolean,
    onMenuLanguageChange: (MenuLanguage) -> Unit,
    onNavigateUp: () -> Unit,
    uiEvent: SettingsScreenUiState.UiEvent?,
    modifier: Modifier = Modifier,
) {
    var menuLanguageDropdownExpanded by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiEvent) {
        if (uiEvent != null) {
            when (uiEvent) {
                is SettingsScreenUiState.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(uiEvent.msg)
                }
            }
        }
    }

    Scaffold(
        modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (!isLoading) {
                        IconButton(onClick = { onNavigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_label)
                            )
                        }
                    }
                },
                title = {
                    Text(
                        stringResource(R.string.settings_label),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
    ) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if (!isLoading) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.menu_language_label)) },
                    trailingContent = {
                        ExposedDropdownMenuBox(
                            menuLanguageDropdownExpanded,
                            onExpandedChange = { menuLanguageDropdownExpanded = it },
                            modifier = Modifier
                                .width(200.dp)
                                .wrapContentWidth()
                        ) {
                            TextField(
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                value = menuLanguage.getDisplayName(context),
                                onValueChange = {},
                                readOnly = true,
                                singleLine = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        menuLanguageDropdownExpanded
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors()
                            )

                            ExposedDropdownMenu(
                                menuLanguageDropdownExpanded,
                                { menuLanguageDropdownExpanded = false }
                            ) {
                                MenuLanguage.entries.forEach { languageOption ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                languageOption.getDisplayName(context),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        onClick = {
                                            onMenuLanguageChange(languageOption)
                                            menuLanguageDropdownExpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                )
                HorizontalDivider()
                val externalLinkIcon = painterResource(R.drawable.external_link)
                val sourceCodeIcon = painterResource(R.drawable.code_icon)
                val languageIcon = painterResource(R.drawable.language_icon)
                val githubLogo = painterResource(R.drawable.github_logo)
                ListItem(
                    headlineContent = { Text(stringResource(R.string.app_language_label)) },
                    Modifier.clickable {
                        // Open system settings to change the app language
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = "package:${context.packageName}".toUri()
                            }
                        )
                    },
                    leadingContent = {
                        Icon(languageIcon, contentDescription = null)
                    },
                    supportingContent = {
                        Text(stringResource(R.string.change_app_language_in_settings_label))
                    },
                    trailingContent = {
                        Icon(
                            externalLinkIcon,
                            contentDescription = stringResource(R.string.open_system_settings_label)
                        )
                    }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text(stringResource(R.string.source_code_label)) },
                    Modifier.clickable {
                        // Open system settings to change the app language
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW).apply {
                                data = context.getString(R.string.github_repo_url).toUri()
                            }
                        )
                    },
                    leadingContent = {
                        Icon(sourceCodeIcon, contentDescription = null)
                    },
                    supportingContent = {
                        Text(stringResource(R.string.source_code_link_desc))
                    },
                    trailingContent = {
                        Icon(
                            githubLogo,
                            contentDescription = stringResource(R.string.open_github_repo_external_link_label),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
            } else {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@PreviewScreenSizes
@PreviewLightDark
//@Preview
@Composable
private fun SettingsScreenPreview() {
    ETHUZHMensaTheme {
        SettingsScreen(MenuLanguage.ENGLISH, false, {}, {}, null)
    }
}