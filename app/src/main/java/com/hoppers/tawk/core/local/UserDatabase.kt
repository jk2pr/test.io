package com.hoppers.tawk.core.local

import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
/**
 * The Room database class for the application.
 * This class provides the DAO for accessing the user data.
 *
 * @property dao The data access object for user entities.
 */
@Database(
    entities = [UserEntity::class],
    version = 1
)
abstract class UserDatabase: IRoomDataBase() {

    abstract override val dao: UserDao
}
/**
 * Abstract base class for the Room database.
 * This class defines the DAO and provides implementation for RoomDatabase methods.
 *
 * @property dao The data access object for user entities.
 */
abstract class IRoomDataBase : RoomDatabase(){
    abstract val dao: UserDao
     override fun clearAllTables() {
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return super.invalidationTracker
    }
    /**
     * Creates a [SupportSQLiteOpenHelper] for the database.
     *
     * @param config The configuration for the database.
     * @return The created [SupportSQLiteOpenHelper].
     */

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        return super.openHelper
    }


}