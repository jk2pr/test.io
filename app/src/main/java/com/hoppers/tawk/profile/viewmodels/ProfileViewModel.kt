package com.hoppers.tawk.profile.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hoppers.tawk.core.local.IRoomDataBase
import com.hoppers.tawk.core.local.UserEntity
import com.hoppers.tawk.core.remote.DispatcherProvider
import com.hoppers.tawk.core.state.UiState
import com.hoppers.tawk.home.data.User
import com.hoppers.tawk.profile.repositories.IProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * ViewModel for the user profile screen.
 *
 * @param dispatchers Provides the coroutine dispatchers.
 * @param userRepository Repository to fetch user details.
 * @param userDatabase Database instance to update user details.
 * @param user The user whose profile is being viewed.
 */
class ProfileViewModel(
    private val dispatchers: DispatcherProvider,
    private val userRepository: IProfileRepository,
    private val userDatabase: IRoomDataBase,
    user: User,
) : ScreenModel {

    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    private val _savingState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState = _uiState.asStateFlow()
    val savingState = _savingState.asStateFlow()

    init {
        send(user.login)
    }

    /**
     * Fetches user details and updates the UI state.
     *
     * @param login The login of the user.
     */
    private fun send(login: String) =
        screenModelScope.launch {
            flow {
                emit(UiState.Loading)
                val mResponse = userRepository.getDetail(login = login)
                emit(UiState.Content(mResponse))
            }.catch {
                emit(UiState.Error(it.message.toString()))
                it.printStackTrace()
            }.flowOn(dispatchers.main)
                .collect {
                    _uiState.value = it
                }
        }

    /**
     * Updates the user in the database and updates the saving state.
     *
     * @param user The user entity to update.
     */
    fun updateUser(user: UserEntity) {
        screenModelScope.launch {
            flow {
                emit(UiState.Loading)
                userDatabase.dao.update(user)
                emit(UiState.Content(user))
            }.catch {
                emit(UiState.Error(it.message.toString()))
                it.printStackTrace()
            }.flowOn(dispatchers.main)
                .collect {
                    _savingState.value = it
                }
        }
    }
}