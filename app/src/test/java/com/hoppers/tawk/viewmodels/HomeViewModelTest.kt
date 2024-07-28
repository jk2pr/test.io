package com.hoppers.tawk.viewmodels

import androidx.paging.Pager
import androidx.paging.PagingData
import app.cash.turbine.test
import com.hoppers.tawk.TestDispatchers
import com.hoppers.tawk.core.state.UiState
import com.hoppers.tawk.core.local.UserEntity
import com.hoppers.tawk.home.mappers.toUserEntity
import com.hoppers.tawk.home.data.User
import com.hoppers.tawk.home.viewmodels.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val pager: Pager<Int, UserEntity> = mockk()
    private val testDispatcher = TestDispatchers()

    private val user =
        User(login = "testuser", note = "testnote", avatarUrl = "", id = 0, url = "")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher.main)
        coEvery { pager.flow } returns flowOf(PagingData.from(listOf(user.toUserEntity())))
        viewModel = HomeViewModel(pager, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state`() = runTest {
        viewModel.pagingFlow.test {
            assertNotNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test filter users`() = runTest {
        val user1 = User(login = "testUser1", note = "Note1")
        val user2 = User(login = "anotherUser", note = "Note2")
        val user3 = User(login = "testUser2", note = "Note3")
        val snapshotList = listOf(user1, user2, user3)

        // Call the doFilter function
        viewModel.doFilter(snapshotList, "test")

        // Collect and assert the filtered users
        viewModel.filteredUsers.test {
            assertEquals(UiState.Content(emptyList<User>()), awaitItem()) // initial empty state

            val content = awaitItem() as UiState.Content
            assertEquals(listOf(user1, user3), content.data)
            cancelAndIgnoreRemainingEvents()
        }
    }


}
