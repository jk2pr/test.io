package com.hoppers.tawk.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.tusharhow.connext.helper.CheckConnectivityStatus
import com.tusharhow.connext.helper.connectivityStatus
import com.tusharhow.connext.models.ConnectionStatus

@Composable
fun Page(
    title: @Composable () -> Unit = {},
    menuItems: List<DropdownMenuItemContent> = emptyList(),
    appBar: @Composable () -> Unit = { AppBar(menuItems = menuItems, title = title) },
    floatingActionButton: @Composable () -> Unit = {},
    contentAlignment: Alignment = Alignment.Center,
    retryHandler: () -> Unit = {},
    content: @Composable () -> Unit
) {

    val connection by connectivityStatus()
    val isConnected = connection === ConnectionStatus.Connected
    val prevStatus = remember { mutableStateOf(isConnected) }

    val snackBarHostState = LocalSnackBarHostState.current

    Scaffold(
        topBar =  appBar ,
        floatingActionButton = floatingActionButton,
        snackbarHost = {
            SnackbarHost(
                hostState = LocalSnackBarHostState.current,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Bottom)
            )
        }
    ) { value ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = value.calculateTopPadding()),
            contentAlignment = contentAlignment,

            content = {

                CheckConnectivityStatus(
                    connectedContent = {
                        if (prevStatus.value != isConnected)
                            LaunchedEffect(Unit) {
                                snackBarHostState.showSnackbar("Connected")
                                prevStatus.value = true
                                retryHandler.invoke()
                            }
                    },
                    disconnectedContent = {
                            LaunchedEffect(Unit) {
                                snackBarHostState.showSnackbar("Disconnected")

                            }}
                )
                content()
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(menuItems: List<DropdownMenuItemContent>, title: @Composable () -> Unit) {
    val navigator = LocalNavigator.current
    val isRootScreen = navigator?.items?.firstOrNull() == navigator?.lastItemOrNull

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        title = title,
        navigationIcon = { if (!isRootScreen) NavigationIcon() },
        actions = {
            menuItems.forEach {
                it.menu()
            }
        }
    )
}

@Composable
private fun NavigationIcon() {
    val navigator = LocalNavigator.current
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "",
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable {
                navigator?.pop()
            }
    )
}

data class DropdownMenuItemContent(var menu: @Composable () -> Unit)