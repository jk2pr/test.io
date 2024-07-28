package com.hoppers.tawk.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSnackBarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No LocalScaffold provided")
}

val LocalOrientationMode = compositionLocalOf<Int> {
    error("No LocalOrientationMode provided")
}
