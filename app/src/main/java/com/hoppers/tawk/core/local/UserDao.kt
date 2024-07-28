package com.hoppers.tawk.core.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

/**
 * Data Access Object (DAO) interface for accessing user data in the database.
 */
@Entity
@Dao
interface UserDao {

    /**
     * Inserts or updates a list of [UserEntity] objects in the database.
     *
     * @param users The list of users to insert or update.
     */
    @Upsert
    suspend fun upsertAll(users: List<UserEntity>)
    /**
     * Updates a single [UserEntity] in the database.
     *
     * @param user The user entity to update.
     */
    @Update
    suspend fun update(user: UserEntity)
    /**
     * Provides a paging source for [UserEntity] objects from the database.
     *
     * @return A [PagingSource] for user entities.
     */
    @Query("SELECT * FROM userentity")
     fun pagingSource(): PagingSource<Int, UserEntity>
    /**
     * Deletes all [UserEntity] objects from the database.
     */
    @Query("DELETE FROM userentity")
    suspend fun clearAll()
}