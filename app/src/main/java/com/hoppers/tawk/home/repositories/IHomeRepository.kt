package com.hoppers.tawk.home.repositories

import com.hoppers.tawk.home.data.User

/**
 * Interface for a repository that provides user data.
 */
interface IHomeRepository {
    /**
     * Fetches a list of users from the server.
     *
     * @param page The page number to fetch users from.
     * @return A list of [User] objects.
     * @throws Exception If there is an error during the network request.
     */
    suspend fun getUser(page: Int): List<User>
}