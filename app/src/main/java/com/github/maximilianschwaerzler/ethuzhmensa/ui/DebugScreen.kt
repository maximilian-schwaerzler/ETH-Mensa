package com.github.maximilianschwaerzler.ethuzhmensa.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.maximilianschwaerzler.ethuzhmensa.data.DebugScreenViewModel
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(
    modifier: Modifier = Modifier,
    viewModel: DebugScreenViewModel = hiltViewModel(),
) {
    Scaffold(
        Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(Modifier.fillMaxSize()) {
                Button(onClick = { viewModel.updateFacilityInfoDBNet() }) {
                    Text("Fetch mensa infos from web")
                }
//                Spacer(Modifier.height(8.dp))
//                Button(onClick = { isRefreshing = true }) {
//                    Text("Load all menus from the web for today")
//                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { viewModel.reloadAllMenusForDate(LocalDate.of(2025, 3, 12)) }) {
                    Text("Reload all menus from the DB for 2025-03-12")
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.purgeDB()
                        viewModel.reloadAllMenusForToday()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Purge DB")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { viewModel.deleteOlderThanToday() }) {
                    Text("Delete all menus older than today")
                }
                Spacer(Modifier.height(8.dp))
                // val facilities by viewModel.facilities.collectAsStateWithLifecycle()
                val menus by viewModel.offers.collectAsStateWithLifecycle()
                val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
                LaunchedEffect(menus) {
                    Log.d("OverviewScreen", "Menus updated")
                }
                PullToRefreshBox(
                    isRefreshing,
                    onRefresh = viewModel::refreshTrigger,
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(menus) {
                            Text(it.toString() + "\n")
                        }
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
private fun DebugScreenPreview() {
    ETHUZHMensaTheme {
        DebugScreen()
    }
}