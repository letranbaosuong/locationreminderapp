package com.letranbaosuong.locationreminderapp.locationreminders.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.letranbaosuong.locationreminderapp.locationreminders.data.dto.ReminderDTO

/**
 * The Room Database that contains the reminders table.
 */
@Database(entities = [ReminderDTO::class], version = 1, exportSchema = false)
abstract class RemindersDatabase : RoomDatabase() {
    companion object {

        private const val DATABASE_NAME = "locationReminders.db"

        private var instance: RemindersDatabase? = null

        private fun create(context: Context): RemindersDatabase =
            Room.databaseBuilder(context, RemindersDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()


        fun getInstance(context: Context): RemindersDatabase =
            (instance ?: create(context)).also { instance = it }
    }

    abstract fun reminderDao(): RemindersDao
}