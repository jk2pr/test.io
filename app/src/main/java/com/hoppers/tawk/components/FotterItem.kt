package com.hoppers.tawk.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp



@Composable
fun FooterItem(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
    ) {
        Text(
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}