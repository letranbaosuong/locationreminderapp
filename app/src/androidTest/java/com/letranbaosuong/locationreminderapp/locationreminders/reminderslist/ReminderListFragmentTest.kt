package com.letranbaosuong.locationreminderapp.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.letranbaosuong.locationreminderapp.R
import com.letranbaosuong.locationreminderapp.locationreminders.RemindersActivity
import com.letranbaosuong.locationreminderapp.locationreminders.data.ReminderDataSource
import com.letranbaosuong.locationreminderapp.locationreminders.data.dto.ReminderDTO
import com.letranbaosuong.locationreminderapp.locationreminders.data.local.LocalDB
import com.letranbaosuong.locationreminderapp.locationreminders.data.local.RemindersLocalRepository
import com.letranbaosuong.locationreminderapp.locationreminders.savereminder.SaveReminderViewModel
import com.letranbaosuong.locationreminderapp.util.DataBindingIdlingResource
import com.letranbaosuong.locationreminderapp.util.monitorActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.endsWith
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

///    TODO: test the navigation of the fragments.
///    TODO: test the displayed data on the UI.
///    TODO: add testing for the error messages.

    private lateinit var reminderDataSource: ReminderDataSource
    private lateinit var application: Application

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun initialKoin() {
        stopKoin()
        application = getApplicationContext()
        val testMyModules = module {
            viewModel {
                RemindersListViewModel(
                    application, get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    application, get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(application) }
        }
        startKoin { modules(testMyModules) }
        reminderDataSource = GlobalContext.get().get()
        runBlocking {
            reminderDataSource.deleteAllReminders()
        }
    }

    @Test
    fun testNavigateToSaveLocation() {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        Espresso.onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
        Thread.sleep(2000)
    }

    @Test
    fun testDisplayedDataListReminder() {
        val reminder1 = ReminderDTO(
            "New York City",
            "New York",
            "New_York",
            21.02914391299968,
            105.83576090149242,
        )
        val reminder2 = ReminderDTO(
            "Ho Chi Minh City",
            "Ho Chi Minh",
            "HCM",
            10.831510969661842,
            106.71300026978942,
        )
        runBlocking {
            reminderDataSource.saveReminder(reminder1)
            reminderDataSource.saveReminder(reminder2)
        }
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)

        Espresso.onView(withText(reminder1.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminder1.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminder1.location))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(withText(reminder2.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminder2.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminder2.location))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(2000)
    }

    @Test
    fun testShowNoDataMessage() {
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)
//        Espresso.onView(withId(R.string.no_data))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(endsWith("No data")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(2000)
    }
}