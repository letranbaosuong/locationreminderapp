package com.letranbaosuong.locationreminderapp.locationreminders.reminderslist

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
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
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

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

    @Rule
    @JvmField
    var activityScenarioRule = ActivityScenarioRule(RemindersActivity::class.java)

    @Before
    fun initialKoin() {
        stopKoin()
        application = getApplicationContext()
        val testModules = module {
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


        startKoin { modules(testModules) }

        reminderDataSource = GlobalContext.get().get()
        runBlocking {
            reminderDataSource.deleteAllReminders()
        }
    }

    @Before
    fun registerIdlingRegistry() {
        IdlingRegistry.getInstance()
            .register(dataBindingIdlingResource)
    }

    @Test
    fun testDisplayedDataListReminder() {
        val reminder1 = ReminderDTO(
            "Ha Noi Capital",
            "Capital",
            "Hanoi",
            21.028511,
            105.804817
        )
        val reminder2 = ReminderDTO(
            "Ho Chi Minh city",
            "Sai Gon",
            "saigon",
            10.762622,
            106.660172
        )
        runBlocking {
            reminderDataSource.saveReminder(reminder1)
            reminderDataSource.saveReminder(reminder2)
        }
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)

        Espresso.onView(withText(reminder2.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminder2.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminder2.location))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(withText(reminder1.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminder1.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminder1.location))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testShowsNoDataMessage() {
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)
//        Espresso.onView(withId(R.string.no_data))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(endsWith("No data")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @After
    fun unregisterIdlingRegistry() {
        IdlingRegistry.getInstance()
            .unregister(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().apply {
            unregister(dataBindingIdlingResource)
        }
    }
}