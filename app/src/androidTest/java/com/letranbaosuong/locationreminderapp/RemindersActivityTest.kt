package com.letranbaosuong.locationreminderapp

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.letranbaosuong.locationreminderapp.locationreminders.RemindersActivity
import com.letranbaosuong.locationreminderapp.locationreminders.data.ReminderDataSource
import com.letranbaosuong.locationreminderapp.locationreminders.data.local.LocalDB
import com.letranbaosuong.locationreminderapp.locationreminders.data.local.RemindersLocalRepository
import com.letranbaosuong.locationreminderapp.locationreminders.reminderslist.RemindersListViewModel
import com.letranbaosuong.locationreminderapp.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var reminderDataSource: ReminderDataSource
    private lateinit var application: Application

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
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
    fun testShowErrorMessageSnackBar() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        Thread.sleep(1000)
        onView(withId(R.id.addReminderFAB)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.saveReminder)).perform(click())
        Thread.sleep(1000)
        val messageError = application.getString(R.string.err_enter_title)
        Thread.sleep(1000)
        onView(withText(messageError)).check(matches(isDisplayed()))
        Thread.sleep(2000)
        activityScenario.close()
    }


    @Test
    fun testSaveReminderVerifyShowSuccessMessageSnackBar() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        onView(withId(R.id.addReminderFAB)).perform(click())
        val editTextTitle = onView(withId(R.id.reminderTitle))
        editTextTitle.perform(
            ViewActions.replaceText("Text Title"), ViewActions.closeSoftKeyboard()
        )
        Thread.sleep(1000)
        val editTextDescription = onView(withId(R.id.reminderDescription))
        editTextDescription.perform(
            ViewActions.replaceText("Text Description"), ViewActions.closeSoftKeyboard()
        )
        Thread.sleep(1000)
        val textViewSelectLocation = onView(withId(R.id.selectLocation))
        textViewSelectLocation.perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.mapSelectLocation)).perform(click())
        Thread.sleep(2000)
        val buttonSelectLocation = onView(withId(R.id.btnSave))
        buttonSelectLocation.perform(click())
        val floatingActionButtonSaveReminder = onView(withId(R.id.saveReminder))
        floatingActionButtonSaveReminder.perform(click())
        val messageAddSuccess = application.getString(R.string.geofences_added)
        onView(withText(messageAddSuccess)).check(matches(isDisplayed()))
        Thread.sleep(2000)
        activityScenario.close()
    }

    @Test
    fun testSaveReminderVerifyShowErrorMessageSnackBar() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.addReminderFAB)).perform(click())
        val title = "New York"
        onView(withId(R.id.reminderTitle)).perform(ViewActions.typeText(title))
        val description = "New York City"
        onView(withId(R.id.reminderDescription)).perform(ViewActions.typeText(description))
        Espresso.closeSoftKeyboard()
        Thread.sleep(1000)
        onView(withId(R.id.saveReminder)).perform(click())
        val messageErrorSelectLocation = application.getString(R.string.err_select_location)
        onView(withText(messageErrorSelectLocation)).check(matches(isDisplayed()))
        Thread.sleep(2000)
        activityScenario.close()
    }
}
