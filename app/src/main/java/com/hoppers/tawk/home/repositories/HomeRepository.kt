package com.hoppers.tawk.home.repositories

import com.hoppers.tawk.home.data.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Repository implementation for fetching user data.
 *
 * @param client The HTTP client used to make network requests.
 */

class HomeRepository(private val client: HttpClient) : IHomeRepository {
    /**
     * Fetches a list of users from the server.
     *
     * @param page The page number to fetch users from.
     * @return A list of [User] objects.
     * @throws Exception If there is an error during the network request.
     */

    override suspend fun getUser(page: Int): List<User> {
            return client.get {
                parameter("since", page)
            }.body()
        }
}