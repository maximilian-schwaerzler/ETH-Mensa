/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.MockData
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.pricesToString
import java.time.LocalDate

class MensaWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            GlanceTheme(colors = GlanceTheme.colors) {
                MensaWidgetCard(
                    MockData.facilities.first(),
                    MockData.offers.first(),
                    { _, _ -> }
                )
            }
        }
    }
}

class WidgetTheme {
    companion object {
        val titleLarge = TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
        )

        val bodyLarge = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.SansSerif
        )

        val bodyMedium = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.SansSerif
        )

        val bodySmall = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
fun MensaWidgetCard(
    facility: Facility,
    offer: OfferWithPrices?,
    onClick: (facilityId: Int, date: LocalDate) -> Unit,
    modifier: GlanceModifier = GlanceModifier,
) {
    Column(GlanceModifier.fillMaxSize().background(GlanceTheme.colors.widgetBackground)) {
        Row(
            GlanceModifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.Start
        ) {
            Text(
                facility.name,
                style = WidgetTheme.titleLarge,
                modifier = GlanceModifier.padding(end = 8.dp)
            )
            Text(
                facility.location,
                style = WidgetTheme.bodyMedium,
                modifier = GlanceModifier.padding(end = 8.dp)
            )
        }
//        HorizontalDivider(
//            GlanceModifier.padding(vertical = 8.dp),
//            thickness = 2.dp,
//            color = MaterialTheme.colorScheme.onPrimary
//        )
        Column(
            GlanceModifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp)
        ) {
            if (offer != null) {
                Column(verticalAlignment = Alignment.Top) {
                    offer.menus.forEach { menu ->
                        Column(GlanceModifier.padding(bottom = 6.dp)) {
                            Text(menu.menu.mealName, style = WidgetTheme.bodyLarge)
                            Text(
                                pricesToString(menu.prices),
                                style = WidgetTheme.bodySmall
                            )
                        }
                    }
                }
            } else {
                Text(
                    LocalContext.current.getString(R.string.no_offer_available_for_today_plural),
                    style = WidgetTheme.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
private fun MensaCardWidgetPreview() {
    MensaWidgetCard(
        MockData.facilities.first(),
        MockData.offers.first(),
        { _, _ -> }
    )
}