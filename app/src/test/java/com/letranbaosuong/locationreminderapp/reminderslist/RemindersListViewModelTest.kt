package com.letranbaosuong.locationreminderapp.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.letranbaosuong.locationreminderapp.AppMainCoroutineRule
import com.letranbaosuong.locationreminderapp.data.FakeDataSource
import com.letranbaosuong.locationreminderapp.locationreminders.data.dto.ReminderDTO
import com.letranbaosuong.locationreminderapp.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    private lateinit var reminderFakeDataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var appMainCoroutineRule = AppMainCoroutineRule()

    @After
    fun autoClose() {
        stopKoin()
    }

    @Before
    fun initViewModel() {
        reminderFakeDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(
            getApplicationContext(),
            reminderFakeDataSource,
        )
    }

    @Test
    fun testShouldReturnError() = runTest {
        reminderFakeDataSource.setShouldReturnError(true)
        saveReminder()
        remindersListViewModel.loadReminders()
        MatcherAssert.assertThat(
            remindersListViewModel.showSnackBar.value, `is`("Test Error Message")
        )
    }

    @Test
    fun check_loading() = runTest(TestCoroutineScheduler()) {
        appMainCoroutineRule.pauseDispatcher()
        saveReminder()
        remindersListViewModel.loadReminders()
        MatcherAssert.assertThat(remindersListViewModel.showLoading.value, `is`(true))
        appMainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(remindersListViewModel.showLoading.value, `is`(false))
    }

    private suspend fun saveReminder() {
        reminderFakeDataSource.saveReminder(
            ReminderDTO(
                "TP HCM",
                "Ho Chi Minh City",
                "Location Detail",
                10.81438451156148,
                106.71517781286005,
            )
        )
    }
}