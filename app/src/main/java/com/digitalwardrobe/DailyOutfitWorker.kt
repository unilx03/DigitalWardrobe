import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.digitalwardrobe.MainActivity
import android.Manifest
import android.content.pm.PackageManager
import android.view.View
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.digitalwardrobe.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyNotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val channelId = "DAILY_OUTFIT"

        // Check notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted; skip sending the notification
            return Result.success()
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val selectedDays = prefs.getStringSet("dailyActiveDays", emptySet()) ?: emptySet()

        val today = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
        if (!selectedDays.contains(today)) {
            //today not selected in preferences
            return Result.success()
        }

        createNotificationChannel(channelId)

        // Intent to open MainActivity when the notification is tapped
        val newIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("caller", "notification")
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, newIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build notification with pending intent
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Add your daily outfit")
            .setContentText("Don't forget to add your outfit for today!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(100, notification)

        return Result.success()
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Outfit Reminder"
            val descriptionText = "Notification for daily outfit"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}