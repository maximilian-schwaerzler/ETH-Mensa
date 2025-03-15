package com.github.maximilianschwaerzler.ethuzhmensa.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.DailyOfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.MockData
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme

@Composable
fun MensaOverviewCard(
    facility: Facility,
    offer: DailyOfferWithPrices?,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .padding(8.dp, 8.dp, 8.dp, 0.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    facility.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    facility.location,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            HorizontalDivider(
                Modifier.padding(vertical = 8.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.surfaceDim
            )
            Column(
                Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
            ) {
                if (offer != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        offer.menus.forEach { menu ->
                            Column {
                                Text(menu.menu.mealName, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    menu.prices.joinToString(" / ") { "${it.price} CHF" },
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        "No offer available for today",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MensaOverviewCardPreview() {
    ETHUZHMensaTheme {
        Column(Modifier.padding(8.dp)) {
            MensaOverviewCard(
                facility = MockData.facilities.first(),
                offer = MockData.offers.first()
            )
        }
    }
}