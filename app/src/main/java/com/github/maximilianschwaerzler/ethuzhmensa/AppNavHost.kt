package com.github.maximilianschwaerzler.ethuzhmensa

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.maximilianschwaerzler.ethuzhmensa.data.OverviewScreenViewModel
import com.github.maximilianschwaerzler.ethuzhmensa.ui.DebugScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.OverviewScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
object SplashScreen

@Serializable
object DebugScreen

@Serializable
object OverviewScreen

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController, SplashScreen) {
        composable<SplashScreen> {
            SplashScreen {
                navController.navigate(
                    OverviewScreen,
                    NavOptions.Builder().setPopUpTo(SplashScreen, true).build()
                )
            }
        }
        composable<DebugScreen> { DebugScreen() }
        composable<OverviewScreen> {
            val viewModel: OverviewScreenViewModel = hiltViewModel()
            val isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle()
            val facilitiesWithOffers = viewModel.facilitiesWithOffers.collectAsStateWithLifecycle()

            OverviewScreen(
                isRefreshing = isRefreshing.value,
                facilitiesWithOffers = facilitiesWithOffers.value,
                onRefresh = viewModel::onRefresh
            )
        }
    }
}