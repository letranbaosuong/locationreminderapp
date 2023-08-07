package com.letranbaosuong.locationreminderapp.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.letranbaosuong.locationreminderapp.AppMainCoroutineRule
import com.letranbaosuong.locationreminderapp.data.FakeDataSource
import com.letranbaosuong.locationreminderapp.locationreminders.data.dto.ReminderDTO
import com.letranbaosuong.locationreminderapp.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//@RunWith(AndroidJUnit4::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    private lateinit var fakeReminderDataSource: FakeDataSource
    private lateinit var remindersViewModel: RemindersListViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = AppMainCoroutineRule()


    @Before
    fun setupViewModel() {
        fakeReminderDataSource = FakeDataSource()
        remindersViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeReminderDataSource
        )
    }

    @Test
    fun testShouldReturnError() = runBlockingTest {
        fakeReminderDataSource.setShouldReturnError(true)
        saveReminderFakeData()
        remindersViewModel.loadReminders()

        MatcherAssert.assertThat(
            remindersViewModel.showSnackBar.value, `is`("Reminders not found")
        )
    }

    @Test
    fun check_loading() = runBlockingTest {

        mainCoroutineRule.pauseDispatcher()
        saveReminderFakeData()
        remindersViewModel.loadReminders()

        MatcherAssert.assertThat(remindersViewModel.showLoading.value, `is`(true))

        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(remindersViewModel.showLoading.value, `is`(false))
    }

    private suspend fun saveReminderFakeData() {
        fakeReminderDataSource.saveReminder(
            ReminderDTO(
                "title abc",
                "description abc",
                "location abc",
                77.00,
                77.00,
            )
        )
    }

}