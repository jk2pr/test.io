package com.hoppers.tawk.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration


/**
 * A composable function that wraps content with several CompositionLocals.
 *
 * This function provides several local values to the content composable, such as
 * the current screen orientation, connectivity status, and a snackbar host state.
 *
 * @param content A composable lambda that will be wrapped by the provided CompositionLocals.
 */
@Composable
fun ComposeLocalWrapper(content: @Composable () -> Unit) {
    val orientation = LocalConfiguration.current.orientation

    CompositionLocalProvider(
        LocalSnackBarHostState provides remember { SnackbarHostState() },
        LocalOrientationMode provides remember { orientation },
        content = content
    )
}
