package com.hoppers.tawk.profile.repositories

import com.hoppers.tawk.profile.data.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path

/**
 * Repository class for fetching user profile details from a remote API.
 *
 * @property client The HttpClient instance used for making network requests.
 */
class ProfileRepository(private val client: HttpClient): IProfileRepository {
    /**
     * Fetches the user profile details for the specified login.
     *
     * @param login The login of the user whose profile details are to be fetched.
     * @return The UserProfile object containing the user's profile details.
     * @throws Exception if the network request fails.
     */
    override suspend fun getDetail(login: String): UserProfile {
        return client.get {
            url {
                path("users/$login")
            }
        }.body()
    }

}
