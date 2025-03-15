package com.github.maximilianschwaerzler.ethuzhmensa.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.DailyOfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.MockData
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme
import com.github.maximilianschwaerzler.ethuzhmensa.ui.utils.MensaOverviewCard

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun OverviewScreen(
    isRefreshing: Boolean,
    facilitiesWithOffers: List<Pair<Facility, DailyOfferWithPrices?>>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier) { paddingValues ->
        PullToRefreshBox(
            isRefreshing,
            onRefresh,
            Modifier.padding(paddingValues)
        ) {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    facilitiesWithOffers.filterNot { it.second == null }.sortedBy { it.first.id },
                    key = { it.first.id }
                ) {
                    MensaOverviewCard(it.first, it.second)
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
        OverviewScreen(
            isRefreshing = false,
            facilitiesWithOffers = MockData.facilitiesWithOffers,
            onRefresh = {}
        )
    }
}