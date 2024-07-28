package com.hoppers.tawk.home.viewmodels

import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hoppers.tawk.core.local.UserEntity
import com.hoppers.tawk.core.remote.DispatcherProvider
import com.hoppers.tawk.core.state.UiState
import com.hoppers.tawk.home.data.User
import com.hoppers.tawk.home.mappers.toUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen, responsible for managing and providing user data.
 *
 * @property pager The Pager instance for pagination of UserEntity objects.
 * @property dispatcherProvider The DispatcherProvider instance for coroutine dispatchers.
 */

class HomeViewModel(
    val pager: Pager<Int, UserEntity>,
    private val dispatcherProvider: DispatcherProvider

) : ScreenModel {
    /**
     * A flow of paginated user data, converted from UserEntity to User.
     */

    val pagingFlow = pager.flow.map { pagingData -> pagingData.map { it.toUser() } }
        .cachedIn(screenModelScope)

    private val _filteredUsers = MutableStateFlow<UiState>(UiState.Content(emptyList<User>()))
    val filteredUsers = _filteredUsers.asStateFlow()

    /**
     * Filters the list of users based on the provided query and updates the filteredUsers state.
     *
     * @param snapshotList The list of users to filter.
     * @param query The query to filter the users by.
     */

    fun doFilter(snapshotList: List<User>, query: String) {
        screenModelScope.launch {
            flow {
                emit(UiState.Content(emptyList<User>()))
                val filteredList = if (query.isEmpty()) emptyList()
                else snapshotList.filter {
                    it.login.contains(query, ignoreCase = true) ||
                            it.note.contains(query, ignoreCase = true)
                }
                emit(UiState.Content(filteredList))
            }.flowOn(dispatcherProvider.main)
                .collect {
                    _filteredUsers.value = it
                }
        }
    }


}


