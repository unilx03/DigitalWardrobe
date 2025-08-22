package com.digitalwardrobe

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.digitalwardrobe.data.DigitalWardrobeRoomDatabase
import com.digitalwardrobe.data.GeofenceVisit
import com.digitalwardrobe.data.GeofenceVisitRepository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val geofenceEnabled = prefs.getBoolean("geofencingNotification", false)
        val visitLimit = prefs.getInt("geofencingFrequency", 5)

        GeofencingEvent.fromIntent(intent)?.let { event ->
            if (event.hasError()) {
                Log.e("Geofence", "Error: ${event.errorCode}")
                return
            }

            for (geofence in event.triggeringGeofences!!) {
                val requestId = geofence.requestId
                Log.d("Geofence", "Triggered: ${geofence.requestId}")

                if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    Log.i("Geofence", "Entered geofence")
                    CoroutineScope(Dispatchers.IO).launch {
                        if (!geofenceEnabled) return@launch

                        val repo = GeofenceVisitRepository(
                            DigitalWardrobeRoomDatabase.getDatabase(context).geofenceVisitDao()
                        )

                        val visit = GeofenceVisit(
                            requestId = requestId,
                            timestamp = System.currentTimeMillis()
                        )
                        repo.insert(visit)

                        val oneMonthAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000) // 30 days in ms

                        // Count visits in last month
                        val visitCount = repo.countVisitsSince(requestId, oneMonthAgo)
                        Log.d("Geofence", "Visit count in last month: $visitCount")

                        if (visitCount > visitLimit) {
                            sendNotification(
                                context,
                                "You’ve visited this location $visitCount times in the last month!"
                            )
                        }
                    }
                }
                else if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    Log.i("Geofence", "Exit geofence")
                }
            }
        }
    }

    private fun sendNotification(context: Context, message: String) {
        val channelId = "Geofence Channel"

        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Geofence Frequency Alert",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification when entering geofenced areas often"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.location_on_24px)
            .setContentTitle("Nearby Purchase Location")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}