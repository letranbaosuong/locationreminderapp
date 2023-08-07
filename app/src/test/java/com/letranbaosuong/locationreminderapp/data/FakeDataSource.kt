package com.letranbaosuong.locationreminderapp.data

import com.letranbaosuong.locationreminderapp.locationreminders.data.ReminderDataSource
import com.letranbaosuong.locationreminderapp.locationreminders.data.dto.ReminderDTO
import com.letranbaosuong.locationreminderapp.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
@Suppress("UNREACHABLE_CODE")
class FakeDataSource : ReminderDataSource {


    //    TODO: Create a fake data source to act as a double to the real data source
    val reminders = mutableListOf<ReminderDTO>()
    var isError = false
    val errorMsg = "Reminder Error"

    private var shouldReturnError = false

    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        TODO("Return the reminders")
        return if (isError) Result.Error(errorMsg) else Result.Success(reminders)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        TODO("save the reminder")
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("return the reminder with the id")
        return if (isError) {
            Result.Error(errorMsg)
        } else {
            reminders.firstOrNull { it.id == id }?.let {
                Result.Success(it)
            } ?: Result.Error("Not found data")
        }
    }

    suspend fun deleteReminder(id: String): Boolean {
        return reminders.removeIf { it.id == id }
    }

    override suspend fun deleteAllReminders() {
        TODO("delete all the reminders")
        reminders.clear()
    }
}