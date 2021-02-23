package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        )
                .allowMainThreadQueries()
                .build()

        localDataSource =
                RemindersLocalRepository(
                        database.reminderDao(),
                        Dispatchers.Main
                )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_retrievesAll() = runBlocking {
        // GIVEN - A new task saved in the database.
        val reminder_1 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        localDataSource.saveReminder(reminder_1)
        val reminder_2 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        localDataSource.saveReminder(reminder_2)
        val reminder_3 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        localDataSource.saveReminder(reminder_3)

        val reminders = localDataSource.getReminders() as Result.Success

        assertThat(reminders, not(nullValue()))
        assertThat(reminders.data.size, `is`(3))
        assertThat(reminders.data[0], `is`(reminder_1))
        assertThat(reminders.data[1], `is`(reminder_2))
        assertThat(reminders.data[2], `is`(reminder_3))
    }
}