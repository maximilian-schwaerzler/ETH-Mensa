package com.github.maximilianschwaerzler.ethuzhmensa.utils

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import androidx.window.layout.WindowMetricsCalculator
import com.github.maximilianschwaerzler.ethuzhmensa.R

/**
 * Checks the screen size and prevents the activity from turning when the app is running on a "compact" device (i.e. width < 600 or height < 480)
 *
 * **Temporary solution until all orientations on all devices are supported**
 */
@Composable
fun ScreenOrientationLocker() {
    val activity = LocalActivity.current!!

    /** Determines whether the device has a compact screen. **/
    fun compactScreen(): Boolean {
        val metrics = WindowMetricsCalculator.getOrCreate().computeMaximumWindowMetrics(activity)
        val width = metrics.bounds.width()
        val height = metrics.bounds.height()
        val density = activity.resources.displayMetrics.density
        val windowSizeClass =
            WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(width / density, height / density)

        return !(windowSizeClass.isWidthAtLeastBreakpoint(activity.resources.getInteger(R.integer.config_compact_screen_max_width)) &&
                windowSizeClass.isHeightAtLeastBreakpoint(activity.resources.getInteger(R.integer.config_compact_screen_max_height)))
    }

    LaunchedEffect(LocalConfiguration.current) {
        activity.requestedOrientation = if (compactScreen()) {
            Log.d("ScreenOrientationLocker", "Is Compact screen: true")
            ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        } else {
            Log.d("ScreenOrientationLocker", "Is Compact screen: false")
            ActivityInfo.SCREEN_ORIENTATION_USER
        }
    }
}