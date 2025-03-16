package com.github.maximilianschwaerzler.ethuzhmensa.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.MenuWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.MockData
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.pricesToString
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme

@Composable
fun MenuDetailItem(
    modifier: Modifier = Modifier,
    menu: MenuWithPrices,
    showImage: Boolean = true
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    menu.menu.name.uppercase(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    pricesToString(menu.prices),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
            HorizontalDivider(
                Modifier
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(
                    Modifier
                        .fillMaxWidth(if (showImage) 0.6f else 1f)
                        .padding(bottom = 8.dp)
                        .padding(horizontal = 12.dp)
                        .padding(end = 16.dp)
                ) {
                    Text(
                        menu.menu.mealName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (menu.menu.mealDescription.isNotBlank()) {
                        Text(
                            // TODO: Trim when saving to DB
                            menu.menu.mealDescription.trim(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                if (showImage && menu.menu.imageUrl != null) {
                    AsyncImage(
                        // TODO: Add client-id in a better way
                        model = "${menu.menu.imageUrl}?client-id=ethz-wcms",
                        contentDescription = menu.menu.name,
                        contentScale = ContentScale.Crop,
                        clipToBounds = true,
                        modifier = Modifier.padding(end = 4.dp)
//                            .heightIn(min = 50.dp)
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MenuDetailItemPreview() {
    ETHUZHMensaTheme {
        Scaffold {
            Column(Modifier.padding(it)) {
                MenuDetailItem(
                    menu = MockData.offers.first().menus.first(),
                    showImage = true
                )
            }
        }
    }
}