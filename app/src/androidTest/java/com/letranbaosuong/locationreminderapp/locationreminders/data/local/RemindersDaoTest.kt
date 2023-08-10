package com.letranbaosuong.locationreminderapp.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.letranbaosuong.locationreminderapp.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    //    TODO: Add testing implementation to the RemindersDao.kt
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var remindersDatabase: RemindersDatabase

    @Before
    fun initialDatabase() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @Test
    fun testDataBase_saveReminder() = runTest {
        val reminder = ReminderDTO(
            "TP HCM",
            "Ho Chi Minh City",
            "Location Detail",
            10.81438451156148,
            106.71517781286005,
        )

        remindersDatabase.reminderDao().saveReminder(reminder)
        val loadedDataList = remindersDatabase.reminderDao().getReminders()
        MatcherAssert.assertThat(loadedDataList.size, `is`(1))
        val loadedData = loadedDataList[0]
        MatcherAssert.assertThat(loadedData.id, `is`(reminder.id))
        MatcherAssert.assertThat(loadedData.title, `is`(reminder.title))
        MatcherAssert.assertThat(loadedData.description, `is`(reminder.description))
        MatcherAssert.assertThat(loadedData.location, `is`(reminder.location))
        MatcherAssert.assertThat(loadedData.latitude, `is`(reminder.latitude))
        MatcherAssert.assertThat(loadedData.longitude, `is`(reminder.longitude))

    }

    @Test
    fun testDataBase_deleteAllReminders() = runTest {
        val reminderFirst = ReminderDTO(
            "TP HCM 1",
            "Ho Chi Minh City 1",
            "Location Detail 1",
            10.81438451156148,
            106.71517781286005,
        )
        val reminderSecond = ReminderDTO(
            "TP HCM 1",
            "Ho Chi Minh City 1",
            "Location Detail 1",
            10.81438451156149,
            106.71517781286006,
        )

        remindersDatabase.reminderDao().saveReminder(reminderFirst)
        remindersDatabase.reminderDao().saveReminder(reminderSecond)
        remindersDatabase.reminderDao().deleteAllReminders()

        val dataObtained = remindersDatabase.reminderDao().getReminders()
        MatcherAssert.assertThat(dataObtained, notNullValue())
        MatcherAssert.assertThat(dataObtained, `is`(arrayListOf()))
    }

    @After
    fun closeDatabase() = remindersDatabase.close()
}