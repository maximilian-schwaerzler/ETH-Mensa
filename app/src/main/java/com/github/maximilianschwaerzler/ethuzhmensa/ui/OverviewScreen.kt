package com.github.maximilianschwaerzler.ethuzhmensa.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.maximilianschwaerzler.ethuzhmensa.data.OverviewScreenViewModel
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme

@Composable
fun OverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: OverviewScreenViewModel = hiltViewModel(),
) {
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
            Column(Modifier.fillMaxSize()) {
                Button(onClick = { viewModel.updateFacilityInfoDBNet() }) {
                    Text("Fetch mensa infos from web")
                }
                Button(onClick = { viewModel.updateMenusDBNet() }) {
                    Text("Load all menus from the web for today")
                }
                Button(onClick = { viewModel.loadAllMenusForToday() }) {
                    Text("Reload all menus from the DB for today")
                }
                Button(
                    onClick = { viewModel.purgeDB() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Purge DB")
                }
                val facilities = viewModel.facilities.collectAsStateWithLifecycle()
                val menus = viewModel.offers.collectAsStateWithLifecycle()
//                LazyColumn {
//                    items(facilities.value) {
//                        Text(it.toString() + "\n")
//                    }
//                }

                LazyColumn {
                    items(menus.value) {
                        Text(it.toString() + "\n")
                    }
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