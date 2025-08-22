/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme
import com.github.maximilianschwaerzler.ethuzhmensa.ui.utils.ScreenOrientationLocker
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity of the app.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScreenOrientationLocker()
            ETHUZHMensaTheme {
                AppNavHost()
            }
        }
    }
}