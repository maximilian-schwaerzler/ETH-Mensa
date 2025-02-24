package com.github.maximilianschwaerzler.ethuzhmensa.ui

import android.content.res.Configuration
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
            val context = LocalContext.current
            LaunchedEffect(true) {
//                val result = MensaMenuManager(9).fetchMenuJson()
//                result
//                    .onSuccess {
//                        Log.d("OverviewScreen", it.toString())
//                    }
//                    .onFailure {
//                        when (it) {
//                            is UnknownHostException -> {
//                                Toast.makeText(context, "No network connection", Toast.LENGTH_LONG)
//                                    .show()
//                            }
//
//                            else -> Log.e("OverviewScreen", null, it)
//                        }
//                    }
//                val facilities = List(14) {
//                    val facility = Facility(it + 1)
//                    facility.fetchFacilityInformation()
//                    return@List facility
//                }
//
//                for (facility in facilities) {
//                    Log.d("OverviewScreen", facility.facilityInfo.toString())
//                }
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