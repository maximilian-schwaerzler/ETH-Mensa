package com.github.maximilianschwaerzler.ethuzhmensa.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.maximilianschwaerzler.ethuzhmensa.data.MensaMenuManager
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme

@Composable
fun OverviewScreen(modifier: Modifier = Modifier) {
    Scaffold(
        Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("This is the Overview Screen")
            LaunchedEffect(true) {
                MensaMenuManager(9).fetchMenuJson()?.let {
                    Log.d("OverviewView", it.toString().take(15))
                }
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = true,
    showBackground = true
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun OverviewScreenPreview() {
    ETHUZHMensaTheme {
        OverviewScreen()
    }
}