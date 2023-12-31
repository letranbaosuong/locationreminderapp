package com.letranbaosuong.locationreminderapp.locationreminders.geofence

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.letranbaosuong.locationreminderapp.locationreminders.data.ReminderDataSource
import com.letranbaosuong.locationreminderapp.locationreminders.data.dto.ReminderDTO
import com.letranbaosuong.locationreminderapp.locationreminders.data.dto.Result
import com.letranbaosuong.locationreminderapp.locationreminders.reminderslist.ReminderDataItem
import com.letranbaosuong.locationreminderapp.utils.sendNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

@Suppress("DEPRECATION")
class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {
        private const val JOB_ID = 573

        // TODO: call this to start the JobIntentService to handle the geofencing transition events
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        // TODO: handle the geofencing transition events and
        //  send a notification to the user when he enters the geofence area
        // TODO call @sendNotification
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true) {
            return
        }

        if (geofencingEvent?.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            if (geofencingEvent.triggeringGeofences?.isNotEmpty() == true) {
                sendNotification(geofencingEvent.triggeringGeofences ?: mutableListOf<Geofence>())
            }
        }
    }

    // TODO: get the request id of the current geofence
    private fun sendNotification(triggeringGeofences: List<Geofence>) {
        triggeringGeofences.forEach { triggeringGeofence ->
            val requestId = triggeringGeofence.requestId

            //Get the local repository instance
            //val remindersLocalRepository: RemindersLocalRepository by inject()
            val remindersLocalRepository: ReminderDataSource by inject()
            // Interaction to the repository has to be through a coroutine scope
            CoroutineScope(coroutineContext).launch(SupervisorJob()) {
                //get the reminder with the request id
                val result = remindersLocalRepository.getReminder(requestId)
                if (result is Result.Success<ReminderDTO>) {
                    val reminderDTO = result.data
                    //send a notification to the user with the reminder details
                    sendNotification(
                        this@GeofenceTransitionsJobIntentService, ReminderDataItem(
                            reminderDTO.title,
                            reminderDTO.description,
                            reminderDTO.location,
                            reminderDTO.latitude,
                            reminderDTO.longitude,
                            reminderDTO.id
                        )
                    )
                }
            }
        }
    }
}