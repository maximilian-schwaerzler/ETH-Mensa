package com.github.maximilianschwaerzler.ethuzhmensa.ui.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme

@Composable
fun MensaOverviewCard(modifier: Modifier = Modifier) {
    Card(
        Modifier.size(
            width = 180.dp,
            height = 100.dp
        )
    ) {
        Box(Modifier.fillMaxSize()) {
            Text("Card content", Modifier.align(Alignment.Center))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MensaOverviewCardPreview() {
    ETHUZHMensaTheme {
        MensaOverviewCard()
    }
}