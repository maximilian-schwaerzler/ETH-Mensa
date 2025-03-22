package com.github.maximilianschwaerzler.ethuzhmensa

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.maximilianschwaerzler.ethuzhmensa.data.MensaDetailScreenViewModel
import com.github.maximilianschwaerzler.ethuzhmensa.data.OverviewScreenViewModel
import com.github.maximilianschwaerzler.ethuzhmensa.ui.DebugScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.MensaDetailScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.OverviewScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.SettingsScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
object SplashScreen

@Serializable
object DebugScreen

@Serializable
object OverviewScreen

@Serializable
object SettingsScreen

@Serializable
data class MensaDetailScreen(val facilityId: Int)

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController, SplashScreen, modifier) {
        composable<SplashScreen> {
            SplashScreen {
                navController.navigate(
                    OverviewScreen,
                    NavOptions.Builder().setPopUpTo(SplashScreen, true).build()
                )
            }
        }

        composable<OverviewScreen> {
            val viewModel: OverviewScreenViewModel = hiltViewModel()
            val isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle()
            val facilitiesWithOffers = viewModel.facilitiesWithOffers.collectAsStateWithLifecycle()

            OverviewScreen(
                isRefreshing = isRefreshing.value,
                facilitiesWithOffers = facilitiesWithOffers.value,
                onRefresh = viewModel::onRefresh,
                onSettingsNavigate = { navController.navigate(SettingsScreen) },
                onDetailScreenNavigate = { facilityId ->
                    navController.navigate(
                        MensaDetailScreen(
                            facilityId
                        )
                    )
                }
            )
        }

        composable<SettingsScreen> {
            SettingsScreen { navController.popBackStack() }
        }

        composable<MensaDetailScreen> {
            val viewModel: MensaDetailScreenViewModel = hiltViewModel()
            val facilityId = it.toRoute<MensaDetailScreen>().facilityId
            viewModel.loadFacilityAndMenus(facilityId)
            val facility = viewModel.facility.collectAsStateWithLifecycle()
            val menus = viewModel.menus.collectAsStateWithLifecycle()
            MensaDetailScreen(
                facility.value,
                menus.value,
                onNavigateUp = { navController.popBackStack() })
        }

        composable<DebugScreen> { DebugScreen() }
    }
}