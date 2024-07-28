package com.hoppers.tawk.viewmodels


import app.cash.turbine.test
import com.hoppers.tawk.TestDispatchers
import com.hoppers.tawk.profile.data.UserProfile
import com.hoppers.tawk.core.state.UiState
import com.hoppers.tawk.core.local.IRoomDataBase
import com.hoppers.tawk.core.local.UserDao
import com.hoppers.tawk.core.local.UserEntity
import com.hoppers.tawk.home.data.User
import com.hoppers.tawk.profile.repositories.IProfileRepository
import com.hoppers.tawk.profile.viewmodels.ProfileViewModel
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private val userRepository: IProfileRepository = mockk()
    private val userDatabase: IRoomDataBase = mockk()
    private val userDao: UserDao = mockk()
    private val testDispatcher = TestDispatchers()
    val user = User(login = "testuser")
    val userProfile = UserProfile(name = "Test User")
    val userEntity = UserEntity(login = "testuser", url = "Test User", id = 0, avatarUrl = "", note = "Test User")
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher.io)

        every { userDatabase.dao } returns userDao
        coEvery { userRepository.getDetail("testuser") } returns userProfile
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test send user details success`() = runTest {

        viewModel = ProfileViewModel(testDispatcher, userRepository, userDatabase, user)
      //  viewModel.send("testuser")

        viewModel.uiState.test {
            assertEquals(UiState.Empty, awaitItem())
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Content(userProfile), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test send user details error`() = runTest {
        val errorMessage = "This is Test Error"
        coEvery { userRepository.getDetail("testuser") } throws RuntimeException(errorMessage)

        viewModel = ProfileViewModel(testDispatcher, userRepository, userDatabase, user)

        viewModel.uiState.test {
            assertEquals(UiState.Empty, awaitItem())
            assertEquals(UiState.Loading, awaitItem())
            val error = awaitItem() as UiState.Error
            assertEquals(errorMessage, error.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test update user success`() = runTest {

        coJustRun { userDao.update(userEntity) }

        viewModel = ProfileViewModel(testDispatcher, userRepository, userDatabase, User(login = "testuser"))

        viewModel.updateUser(userEntity)

        viewModel.savingState.test {
            assertEquals(UiState.Empty, awaitItem())
            assertEquals(UiState.Loading, awaitItem())
            //delay(1000)
            assertEquals(UiState.Content(userEntity), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test update user error`() = runTest {
        val errorMessage = "This is Test Error"
        coEvery { userDao.update(userEntity) } throws RuntimeException(errorMessage)

        viewModel = ProfileViewModel(testDispatcher, userRepository, userDatabase, User(login = "testuser"))

        viewModel.updateUser(userEntity)

        viewModel.savingState.test {
            assertEquals(UiState.Empty, awaitItem())
            assertEquals(UiState.Loading, awaitItem())
            val error = awaitItem() as UiState.Error
            assertEquals(errorMessage, error.message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

