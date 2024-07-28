package com.hoppers.tawk.core.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hoppers.tawk.core.local.IRoomDataBase
import com.hoppers.tawk.core.local.UserEntity
import com.hoppers.tawk.home.mappers.toUserEntity
import com.hoppers.tawk.home.repositories.IHomeRepository


/**
 * A RemoteMediator implementation for handling user data pagination.
 *
 * @property userDb The Room database instance for accessing user data.
 * @property repository The repository for fetching user data from a remote source.
 */

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val userDb: IRoomDataBase,
    private val repository: IHomeRepository,
) : RemoteMediator<Int, UserEntity>() {


    /**
     * Loads data for paging.
     *
     * @param loadType The type of load operation (REFRESH, PREPEND, APPEND).
     * @param state The current state of paging.
     * @return The result of the load operation, either success or error.
     */

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>,
    ): MediatorResult {
        return try {
            // Determine the key for loading data based on the load type

            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    0
                }

                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {

                    val lastItem = state.lastItemOrNull()
                    val k = lastItem?.id ?: 0
                    k
                }
            }

            // Fetch users from the repository

            val users = repository.getUser(page = loadKey)
            //Simulating a delay

            //  delay(10000)

            // Perform database transactions

            userDb.withTransaction {
                if (loadType == LoadType.REFRESH)
                    userDb.dao.clearAll()
                val userEntities = users.map { it.toUserEntity() }
                userDb.dao.upsertAll(userEntities)
            }

            MediatorResult.Success(endOfPaginationReached = users.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}