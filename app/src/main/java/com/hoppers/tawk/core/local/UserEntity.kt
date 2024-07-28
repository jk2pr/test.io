package com.hoppers.tawk.core.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a user entity in the database.
 *
 * @property id The primary key ID of the user, auto-generated.
 * @property login The login name of the user.
 * @property url The URL associated with the user.
 * @property note A note associated with the user.
 * @property avatarUrl The URL of the user's avatar.
 */

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val login: String,
    val url: String,
    var note: String,
    val avatarUrl:String
)