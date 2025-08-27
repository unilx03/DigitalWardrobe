package com.digitalwardrobe

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.lifecycleScope
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.digitalwardrobe.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WeatherCheckWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /*private fun getWeatherByLocation(lat: Double, lon: Double) {
        lifecycleScope.launch {
            try {
                val apiKey = getString(context, R.string.weatherAPIKey)
                val response = RetrofitClient.weatherService.getCurrentWeatherByCoords(lat, lon, apiKey)
                val currentTemperature = response.main.temp

                val prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
                prefs.edit()
                    .putFloat("last_known_temp", currentTemperature.toFloat())
                    .putString("last_known_condition", response.weather.firstOrNull()?.main ?: "")
                    .apply()

            } catch (e: Exception) {
                Log.v("Weather","Error: ${e.message}")
            }
        }
    }*/

    override suspend fun doWork(): Result {
        return try {
            val apiKey = context.getString(R.string.weatherAPIKey)
            val prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

            val lastKnownTemp = prefs.getFloat("last_known_temp", Float.NaN)
            val lastKnownCondition = prefs.getString("last_known_condition", null)

            if (lastKnownTemp.isNaN() || lastKnownCondition == null) return Result.success()

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            )
            {
                Log.w("WeatherWorker", "Location permission not granted")
                return Result.failure()
            }

            val location: android.location.Location? = fusedLocationClient
                .getCurrentLocation(
                    com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()

            if (location == null) {
                Log.w("WeatherWorker", "Location is null")
                return Result.failure()
            }

            val lat = location.latitude
            val lon = location.longitude

            val response = RetrofitClient.weatherService.getCurrentWeatherByCoords(lat, lon, apiKey)
            val newTemp = response.main.temp.toFloat()
            val newCondition = response.weather.firstOrNull()?.main ?: ""

            val tempChanged = kotlin.math.abs(newTemp - lastKnownTemp) >= 5f
            val conditionChanged = newCondition != lastKnownCondition

            if (tempChanged || conditionChanged) {
                val channelId = "WEATHER_CHANGE"
                createNotificationChannel(channelId)

                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notificationText = buildString {
                    if (tempChanged)
                        append("Temp changed: $lastKnownTemp°C → $newTemp°C. ")

                    if (conditionChanged)
                        append("Condition changed: $lastKnownCondition → $newCondition.")
                }

                val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.cloud_alert_24px)
                    .setContentTitle("Weather Update")
                    .setContentText(notificationText.trim())
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return Result.failure()
                }
                NotificationManagerCompat.from(context).notify(101, notification)

                prefs.edit()
                    .putFloat("last_known_temp", newTemp)
                    .putString("last_known_condition", newCondition)
                    .apply()
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("WeatherWorker", "Error: ${e.message}")
            Result.failure()
        }
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Check"
            val descriptionText = "Notification for sudden weather change"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}