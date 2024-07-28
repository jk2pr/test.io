package com.hoppers.tawk

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hoppers.tawk.core.local.UserDao
import com.hoppers.tawk.core.local.UserDatabase
import com.hoppers.tawk.core.local.UserEntity
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var database: UserDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setUp() {
        // Create an in-memory database
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UserDatabase::class.java
        ).build()
        userDao = database.dao
    }

    @After
    fun tearDown() {
        // Close the database after each test
        database.close()
    }

    @Test
    fun testInsertAndQuery() = runTest {
        // Given
        val userEntity = UserEntity(
            id = 0,
            login = "testuser",
            url = "http://example.com",
            note = "Test note",
            avatarUrl = "http://example.com/avatar.jpg"
        )

        // When
        userDao.upsertAll(listOf(userEntity))

        // Then
        val result = userDao.pagingSource().load(PagingSource.LoadParams.Refresh(0, 1, false))
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(userEntity.login, page.data.first().login)
    }

    @Test
    fun testUpdate() = runTest {
        // Given
        val userEntity = UserEntity(
            id = 0,  // Initial ID is 0, as it will be auto-generated
            login = "testuser",
            url = "http://example.com",
            note = "Test note",
            avatarUrl = "http://example.com/avatar.jpg"
        )
        userDao.upsertAll(listOf(userEntity))

        // Fetch the inserted entity to get the generated ID
        val insertedEntities =
            userDao.pagingSource().load(PagingSource.LoadParams.Refresh(0, 10, false))
        assertTrue(insertedEntities is PagingSource.LoadResult.Page)
        val page = insertedEntities as PagingSource.LoadResult.Page
        val insertedEntity = page.data.firstOrNull { it.login == "testuser" }
        assertNotNull(insertedEntity)

        // When
        val updatedUserEntity = insertedEntity!!.copy(note = "Updated note")
        userDao.update(updatedUserEntity)

        // Then
        val result = userDao.pagingSource().load(PagingSource.LoadParams.Refresh(0, 2, false))
        assertTrue(result is PagingSource.LoadResult.Page)
        val updatedPage = result as PagingSource.LoadResult.Page
        assertEquals("Updated note", updatedPage.data.first().note)
    }


    @Test
    fun testClearAll() = runTest {
        // Given
        val userEntity = UserEntity(
            id = 0,
            login = "testuser",
            url = "http://example.com",
            note = "Test note",
            avatarUrl = "http://example.com/avatar.jpg"
        )
        userDao.upsertAll(listOf(userEntity))

        // When
        userDao.clearAll()

        // Then
        val result = userDao.pagingSource().load(PagingSource.LoadParams.Refresh(0, 1, false))
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
    }
}
