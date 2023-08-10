package com.letranbaosuong.locationreminderapp.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.letranbaosuong.locationreminderapp.AppMainCoroutineRule
import com.letranbaosuong.locationreminderapp.data.FakeDataSource
import com.letranbaosuong.locationreminderapp.locationreminders.reminderslist.ReminderDataItem
import com.letranbaosuong.locationreminderapp.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    //TODO: provide testing to the SaveReminderView and its live data objects

    private lateinit var reminderFakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

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
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            reminderFakeDataSource,
        )
    }

    @Test
    fun testShouldReturnError() = runTest {
        val result = saveReminderViewModel.validateInputData(createEmptyReminderItem())
        MatcherAssert.assertThat(result, CoreMatchers.`is`(true))
    }

    @Test
    fun check_loading() = runTest(TestCoroutineScheduler()) {
        appMainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(reminderItem())
        MatcherAssert.assertThat(saveReminderViewModel.showLoading.value, CoreMatchers.`is`(true))
        appMainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(saveReminderViewModel.showLoading.value, CoreMatchers.`is`(false))
    }

    private fun createEmptyReminderItem(): ReminderDataItem {
        return ReminderDataItem(
            "",
            "Ho Chi Minh City",
            "Location Detail",
            10.81438451156148,
            106.71517781286005,
        )
    }

    private fun reminderItem(): ReminderDataItem {
        return ReminderDataItem(
            "TP HCM",
            "Ho Chi Minh City",
            "Location Detail",
            10.81438451156148,
            106.71517781286005,
        )
    }
}