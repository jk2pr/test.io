package com.hoppers.tawk.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoppers.tawk.R

@Composable
@Preview
fun OfflineError(modifier: Modifier = Modifier, retryHandler: () -> Unit = {}) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(painterResource(id = R.drawable.wifi_off_48dp), contentDescription = "Error")
        Text(text = "You're offline", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Check your connection", style = MaterialTheme.typography.bodyLarge)

        OutlinedButton(onClick = retryHandler) {
            Text(text = "Try again")
        }
    }

}