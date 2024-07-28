package com.hoppers.tawk.profile.repositories

import com.hoppers.tawk.profile.data.UserProfile

/**
 * Interface for fetching user profile details.
 */
interface IProfileRepository {
    /**
     * Fetches the user profile details for the specified login.
     *
     * @param login The login of the user whose profile details are to be fetched.
     * @return The UserProfile object containing the user's profile details.
     * @throws Exception if the network request fails.
     */
    suspend fun getDetail(login: String): UserProfile
}