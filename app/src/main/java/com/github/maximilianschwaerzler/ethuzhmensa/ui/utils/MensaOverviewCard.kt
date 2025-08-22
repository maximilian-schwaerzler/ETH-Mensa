/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.MockData
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.pricesToString
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme
import java.time.LocalDate

/**
 * A card displaying an overview of a mensa facility, including its name, location, and today's offer.
 *
 * @param facility The facility to display.
 * @param offer The offer for today, or null if no offer is available.
 * @param onClick A callback invoked when the card is clicked, providing the facility ID and date.
 * @param modifier Optional [Modifier] for this card.
 */
@Composable
fun MensaOverviewCard(
    facility: Facility,
    offer: OfferWithPrices?,
    onClick: (facilityId: Int, date: LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        { onClick(facility.id, offer?.offer?.date ?: LocalDate.now()) },
        modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
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
                color = MaterialTheme.colorScheme.onPrimary
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
                                    pricesToString(menu.prices),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        stringResource(R.string.no_offer_available_for_today_plural),
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
                offer = MockData.offers.first(),
                onClick = { _, _ -> }
            )
        }
    }
}