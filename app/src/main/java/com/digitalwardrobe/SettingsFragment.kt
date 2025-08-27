package com.digitalwardrobe

import DailyNotificationWorker
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_screen, rootKey)

        // Request POST_NOTIFICATIONS on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context?.let {
                if (ContextCompat.checkSelfPermission(it, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1001
                    )
                }
            }
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Schedule/cancel workers based on current preferences
        val dailyEnabled = prefs.getBoolean("dailyOutfitNotification", false)
        if (dailyEnabled) {
            scheduleDailyNotification(requireContext())
        } else {
            WorkManager.getInstance(requireContext()).cancelUniqueWork("daily_outfit_notification")
        }

        val weatherEnabled = prefs.getBoolean("weatherNotification", false)
        if (weatherEnabled) {
            weatherCheckNotification(requireContext())
        } else {
            WorkManager.getInstance(requireContext()).cancelUniqueWork("weather_check_notification")
        }

        // Listen for future changes
        prefs.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                "dailyOutfitNotification" -> {
                    val enabled = sharedPreferences.getBoolean(key, false)
                    if (enabled)
                        scheduleDailyNotification(requireContext())
                    else
                        WorkManager.getInstance(requireContext()).cancelUniqueWork("daily_outfit_notification")
                }
                "weatherNotification" -> {
                    val enabled = sharedPreferences.getBoolean(key, false)
                    if (enabled)
                        weatherCheckNotification(requireContext())
                    else
                        WorkManager.getInstance(requireContext()).cancelUniqueWork("weather_check_notification")
                }
                //disabling geofencing blocks sendNotification in GeofenceBroadcastReceiver
            }
        }
    }

    private fun scheduleDailyNotification(context: Context) {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        dueDate.set(Calendar.HOUR_OF_DAY, 9)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.DAY_OF_MONTH, 1)
        }

        val initialDelay = dueDate.timeInMillis - currentDate.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_outfit_notification",
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )
    }

    private fun weatherCheckNotification(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<WeatherCheckWorker>(
            1, TimeUnit.HOURS
        )
            .setInitialDelay(0, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "weather_check_notification",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}