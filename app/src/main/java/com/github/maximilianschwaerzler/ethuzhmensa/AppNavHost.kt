package com.github.maximilianschwaerzler.ethuzhmensa

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.maximilianschwaerzler.ethuzhmensa.ui.OverviewScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
object SplashScreen

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
        composable<OverviewScreen> { OverviewScreen() }
    }
}