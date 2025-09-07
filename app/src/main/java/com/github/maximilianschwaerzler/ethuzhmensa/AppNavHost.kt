/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.maximilianschwaerzler.ethuzhmensa.data.viewmodel.MensaDetailScreenViewModel
import com.github.maximilianschwaerzler.ethuzhmensa.data.viewmodel.OverviewScreenViewModel
import com.github.maximilianschwaerzler.ethuzhmensa.data.viewmodel.SettingsScreenViewModel
import com.github.maximilianschwaerzler.ethuzhmensa.ui.MensaDetailScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.OverviewScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.SettingsScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.SplashScreen
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
object OverviewScreen

@Serializable
object SettingsScreen

@Serializable
data class MensaDetailScreen(
    val facilityId: Int,
    val date: Long
)

@Serializable
object ThirdPartyNotices

/**
 * The navigation host for the app, defining all navigation routes and their corresponding composables.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController, OverviewScreen, modifier) {
        composable<OverviewScreen> {
            val viewModel: OverviewScreenViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()

            if (uiState.value.isInitialLoading) {
                SplashScreen()
            } else {
                OverviewScreen(
                    isLoading = uiState.value.isRefreshing,
                    facilitiesWithOffers = uiState.value.facilitiesWithOffers,
                    onRefresh = viewModel::onPullToRefresh,
                    onSettingsNavigate = {
                        navController.navigate(SettingsScreen)
                    },
                    onDetailScreenNavigate = { facilityId, date ->
                        navController.navigate(
                            MensaDetailScreen(facilityId, date.toEpochDay())
                        )
                    },
                )
            }
        }

        composable<SettingsScreen> {
            val viewModel: SettingsScreenViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            SettingsScreen(
                menuLanguage = uiState.value.menuLanguage,
                isLoading = uiState.value.isLoading,
                onMenuLanguageChange = viewModel::updateMenuLanguage,
                onNavigateUp = navController::popBackStack,
                onNavigateToThirdPartyNotices = { navController.navigate(ThirdPartyNotices) },
                uiEvent = uiState.value.event
            )
        }

        composable<MensaDetailScreen>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                )
            },
        ) { backStackEntry ->
            val viewModel: MensaDetailScreenViewModel = hiltViewModel()
            val mensaDetailScreenRoute = backStackEntry.toRoute<MensaDetailScreen>()
            viewModel.loadFacilityAndMenus(
                mensaDetailScreenRoute.facilityId,
                mensaDetailScreenRoute.date.let { LocalDate.ofEpochDay(it) }
            )
            val facility = viewModel.facility.collectAsStateWithLifecycle()
            val menus = viewModel.menus.collectAsStateWithLifecycle()
            MensaDetailScreen(
                facility.value,
                menus.value,
                onNavigateUp = { navController.popBackStack() },
                setFavourite = { isFavourite ->
                    viewModel.setFavourite(mensaDetailScreenRoute.facilityId, isFavourite)
                },
            )
        }

        composable<ThirdPartyNotices> {
            val libraries by rememberLibraries(R.raw.aboutlibraries)

            Scaffold(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back_label)
                                )
                            }
                        },
                        title = {
                            Text(
                                stringResource(R.string.third_party_notices_label),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                        )
                    )
                }
            ) { innerPadding ->
                Box(
                    Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                ) {
                    LibrariesContainer(libraries, Modifier.fillMaxSize())
                }
            }

        }
    }
}