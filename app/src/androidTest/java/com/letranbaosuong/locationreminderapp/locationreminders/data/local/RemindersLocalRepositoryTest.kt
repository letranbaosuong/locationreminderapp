package com.letranbaosuong.locationreminderapp.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.letranbaosuong.locationreminderapp.locationreminders.data.dto.ReminderDTO
import com.letranbaosuong.locationreminderapp.locationreminders.data.dto.Result.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
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

    //    TODO: Add testing implementation to the RemindersLocalRepository.kt
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersDatabase: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun setup() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
        remindersLocalRepository = RemindersLocalRepository(remindersDatabase.reminderDao())
    }

    @After
    fun cleanUp() = remindersDatabase.close()

    @Test
    fun testRepository_saveReminder() = runBlocking {
        val reminder = ReminderDTO(
            "TP HCM 1",
            "Ho Chi Minh City 1",
            "Location Detail 1",
            10.81438451156149,
            106.71517781286006,
        )

        remindersLocalRepository.saveReminder(reminder)

        val result = remindersLocalRepository.getReminder(reminder.id)

        result as Success
        MatcherAssert.assertThat(result.data != null, `is`(true))

        val loadedData = result.data
        MatcherAssert.assertThat(loadedData.id, `is`(reminder.id))
        MatcherAssert.assertThat(loadedData.title, `is`(reminder.title))
        MatcherAssert.assertThat(loadedData.description, `is`(reminder.description))
        MatcherAssert.assertThat(loadedData.location, `is`(reminder.location))
        MatcherAssert.assertThat(loadedData.latitude, `is`(reminder.latitude))
        MatcherAssert.assertThat(loadedData.longitude, `is`(reminder.longitude))
    }

    @Test
    fun testRepository_returnError() = runBlocking {
        val result = remindersLocalRepository.getReminder("2222")
        val error = (result is Error)
        MatcherAssert.assertThat(error, `is`(true))
    }

}