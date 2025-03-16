package com.github.maximilianschwaerzler.ethuzhmensa.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.github.maximilianschwaerzler.ethuzhmensa.ui.theme.ETHUZHMensaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier, onBackNavigation: () -> Unit) {
    var isLanguageEnabled by remember {
        mutableStateOf(true)
    }
    Scaffold(
        modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onBackNavigation() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Settings", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
    ) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            ListItem({ Text("Language") }, trailingContent = {
                Switch(isLanguageEnabled, onCheckedChange = { isLanguageEnabled = it })
            })
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    ETHUZHMensaTheme {
        SettingsScreen {}
    }
}