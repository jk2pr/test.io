package com.hoppers.tawk.core.state

/**
 * A sealed class representing different UI states.
 */

sealed class UiState {
    data class Content(val data: Any) : UiState()
    data class Error(val message: String) : UiState()
    data object Loading : UiState()
    data object Empty : UiState()
}
