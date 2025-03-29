/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(modifier: Modifier = Modifier, onFinishTimer: () -> Unit = {}) {
    val ethLogoPainter = painterResource(R.drawable.eth_logo)
    val uzhLogoPainter = painterResource(R.drawable.uzh_logo)
    val timerDelay = integerResource(R.integer.config_splash_screen_delay).toLong()
    LaunchedEffect(true) {
        delay(timerDelay)
        onFinishTimer()
    }

    Scaffold(
        modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("ETH/UZH", style = MaterialTheme.typography.displayMedium)
            Text(
                "Mensa",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(32.dp))
            Image(
                ethLogoPainter,
                stringResource(R.string.eth_zurich_logo_label), Modifier.width(350.dp)
            )
            Image(uzhLogoPainter, stringResource(R.string.uzh_logo_label), Modifier.width(150.dp))
        }
    }
}

@PreviewScreenSizes
@PreviewLightDark
@Composable
private fun SplashScreenPrev() {
    ETHUZHMensaTheme {
        SplashScreen()
    }
}