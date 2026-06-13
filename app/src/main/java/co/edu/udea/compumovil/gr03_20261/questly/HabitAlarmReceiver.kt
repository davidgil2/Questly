package co.edu.udea.compumovil.gr03_20261.questly

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class HabitAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitTitle = intent.getStringExtra("HABIT_TITLE") ?: "Activity Reminder"
        val habitId = intent.getLongExtra("HABIT_ID", 0L)
        val habitTime = intent.getStringExtra("HABIT_TIME")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "questly_habit_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for your daily quests"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val tapIntent = Intent(context, HabitTrackerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            habitId.toInt(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Questly: ¡Es hora de la acción!")
            .setContentText("Tu actividad '$habitTitle' está lista para comenzar.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(habitId.hashCode(), notification)

        // Re-programar para el día siguiente
        if (habitTime != null) {
            val habit = Habit(id = habitId, title = habitTitle, time = habitTime, iconName = "", colorValue = 0)
            HabitNotificationManager.scheduleNotification(context, habit)
        }
    }
}
