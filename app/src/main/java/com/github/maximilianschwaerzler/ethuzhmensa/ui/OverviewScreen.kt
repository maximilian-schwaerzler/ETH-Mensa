/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.MockData
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme
import com.github.maximilianschwaerzler.ethuzhmensa.ui.utils.MensaOverviewCard
import java.time.LocalDate

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun OverviewScreen(
    isLoading: Boolean,
    facilitiesWithOffers: List<Pair<Facility, OfferWithPrices?>>,
    onRefresh: () -> Unit,
    onSettingsNavigate: () -> Unit,
    onDetailScreenNavigate: (facilityId: Int, date: LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onSurface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {
                        onSettingsNavigate()
                    }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_label),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isLoading,
            onRefresh,
            Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val filteredSortedFacilities =
                facilitiesWithOffers.filterNot { it.second == null || it.second!!.menus.isEmpty() }
                    .sortedBy { it.first.id }
            if (filteredSortedFacilities.isNotEmpty()) {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        filteredSortedFacilities,
                        key = { it.first.id }
                    ) {
                        MensaOverviewCard(it.first, it.second, onDetailScreenNavigate)
                    }
                }
            } else {
                NoOffersInfoPanel { onRefresh() }
            }
        }
    }
}

@Composable
fun NoOffersInfoPanel(modifier: Modifier = Modifier, onRefresh: () -> Unit) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
//                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(32.dp))
            Text(
                stringResource(R.string.no_offers_available_today_singular),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { onRefresh() }) {
                Text(
                    stringResource(R.string.refresh_label),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@PreviewScreenSizes
@PreviewLightDark
@Composable
private fun OverviewScreenPreview() {
    ETHUZHMensaTheme {
        OverviewScreen(
            isLoading = false,
            facilitiesWithOffers = MockData.facilitiesWithOffers,
            onRefresh = {},
            onSettingsNavigate = {},
            onDetailScreenNavigate = { _, _ -> }
        )
    }
}

@PreviewScreenSizes
@PreviewLightDark
@Composable
private fun OverviewScreenPreviewNoMenus() {
    ETHUZHMensaTheme {
        OverviewScreen(
            isLoading = false,
            facilitiesWithOffers = emptyList(),
            onRefresh = {},
            onSettingsNavigate = {},
            onDetailScreenNavigate = { _, _ -> }
        )
    }
}