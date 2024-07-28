package com.hoppers.tawk.home.mappers

import com.hoppers.tawk.core.local.UserEntity
import com.hoppers.tawk.home.data.User

/**
 * Converts a [User] object to a [UserEntity] object.
 *
 * @return A [UserEntity] object containing the data from the [User] object.
 */

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = id,
        avatarUrl = avatarUrl,
        login = login,
        url = url,
        note = note,
    )
}
/**
 * Converts a [UserEntity] object to a [User] object.
 *
 * @return A [User] object containing the data from the [UserEntity] object.
 */
fun UserEntity.toUser(): User {
    return User(
        id = id,
        avatarUrl = avatarUrl,
        login = login,
        url = url,
        note = note,
    )
}