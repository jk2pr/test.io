package com.hoppers.tawk.home.screens

import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import com.hoppers.tawk.R
import com.hoppers.tawk.components.FooterItem
import com.hoppers.tawk.components.LoadingItem
import com.hoppers.tawk.components.OfflineError
import com.hoppers.tawk.components.Page
import com.hoppers.tawk.components.SearchComponent
import com.hoppers.tawk.components.ShimmerListItem
import com.hoppers.tawk.core.state.UiState
import com.hoppers.tawk.home.data.User
import com.hoppers.tawk.home.viewmodels.HomeViewModel
import com.hoppers.tawk.profile.screens.ProfileScreen
import com.tusharhow.connext.helper.connectivityStatus
import com.tusharhow.connext.models.ConnectionStatus
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.UUID


@Parcelize
class HomeScreen : Screen, Parcelable {
    /**
     * Composable function to display the content of the home screen.
     */

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel = koinScreenModel<HomeViewModel>()
        val pagingFlow: LazyPagingItems<User> = viewModel.pagingFlow.collectAsLazyPagingItems()
        val onItemClick: (user: User) -> Unit = { navigator?.push(ProfileScreen(user = it)) }
        val retryHandler: () -> Unit = { pagingFlow.refresh() }
        val originalItems = pagingFlow.itemSnapshotList.items
        val filteredUsers: UiState by viewModel.filteredUsers.collectAsState()
        val doFilter: (query: String) -> Unit = {
            viewModel.doFilter(snapshotList = originalItems, query = it)
        }

        Page(
            retryHandler = retryHandler,
            appBar = {
                SearchComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onSearch = doFilter,
                    content = {
                        SearchContent(
                            filteredUsers = filteredUsers,
                            onItemClick = onItemClick
                        )
                    }
                )
            },
        ) {
            CreateList(
                userList = pagingFlow,
                retryHandler = retryHandler,
                onItemClick = onItemClick
            )
        }

    }

    @Composable
    fun SearchContent(
        modifier: Modifier = Modifier, filteredUsers: UiState, onItemClick: (url: User) -> Unit
    ) {
        LazyColumn(modifier = modifier) {
            (filteredUsers as? UiState.Content).let { list ->
                val filterUser = list?.data as List<*>
                if (filterUser.isEmpty())
                    item {
                        FooterItem(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(), text = "No User Found"
                        )
                    }
                else
                    items(filterUser) { user ->
                        if (filterUser.indexOf(user) > 0) HorizontalDivider()
                        if (user is User)
                            UserItem(
                                user = user,
                                onItemClick = onItemClick
                            )
                    }
            }
        }
    }

    @Composable
    fun CreateList(
        userList: LazyPagingItems<User>,
        retryHandler: () -> Unit,
        onItemClick: (user: User) -> Unit,
    ) {
        val status by connectivityStatus()
        val size by remember(key1 = Unit) { derivedStateOf { userList.itemCount } }
        val isLoading = remember { mutableStateOf(false) }

        when (size) {
            0 -> when (userList.loadState.refresh) {
                is LoadState.Loading ->
                    if (status is ConnectionStatus.Connected)
                        isLoading.value = true else LoadingItem()

                is LoadState.Error -> {
                    OfflineError(
                        retryHandler = retryHandler,
                        title = "Oops!",
                        message = "Please try again"
                    )
                    isLoading.value = false
                }

                is LoadState.NotLoading -> {}
            }

            in 1..Int.MAX_VALUE -> isLoading.value = false
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(
                count = if (isLoading.value) 15 else userList.itemCount,
                key = { index ->
                    if (userList.itemCount > 0)
                        userList[index]?.id ?: 0 else UUID.randomUUID()
                }
            ) { index ->
                ShimmerListItem(isLoading = isLoading.value, contentAfterLoading = {
                    if (index > 0) HorizontalDivider()
                    userList[index]?.let { u -> UserItem(onItemClick = onItemClick, user = u) }
                })
            }
            item {
                when (userList.loadState.append) {
                    is LoadState.Loading -> LoadingItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    is LoadState.Error ->
                        FooterItem(
                            stringResource(R.string.error_while_loading_next_item),
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        )

                    is LoadState.NotLoading -> {}
                }
            }
        }

    }

    /**
     * Derived state to create a color matrix for image color filter.
     */
    @IgnoredOnParcel
    private val colorMatrix by derivedStateOf {
        floatArrayOf(
            -1f, 0f, 0f, 0f, 255f,
            0f, -1f, 0f, 0f, 255f,
            0f, 0f, -1f, 0f, 255f,
            0f, 0f, 0f, 1f, 0f
        )
    }

    @IgnoredOnParcel
    private val colorFilter = ColorFilter.colorMatrix(colorMatrix = ColorMatrix(colorMatrix))

    /**
     * Composable function to display a single user item in the list.
     *
     * @param user The user to display.
     * @param onItemClick The function to call when the user item is clicked.
     */
    @Composable
    private fun UserItem(user: User, onItemClick: (url: User) -> Unit = {}) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(8.dp)
                .clickable { onItemClick(user) }
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                colorFilter = if (user.id % 4 == 0) colorFilter else null
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = user.login,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    if (user.note.isNotEmpty())
                        Icon(
                            painter = painterResource(id = R.drawable.sticky_note),
                            contentDescription = "...",
                        )
                }
                Text(
                    text = user.id.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

    }
}