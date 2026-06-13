package co.edu.udea.compumovil.gr03_20261.questly

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object HabitNotificationManager {

    fun scheduleNotification(context: Context, habit: Habit) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, HabitAlarmReceiver::class.java).apply {
            putExtra("HABIT_TITLE", habit.title)
            putExtra("HABIT_ID", habit.id)
            putExtra("HABIT_TIME", habit.time)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habit.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = parseTimeRobust(habit.time) ?: return
        
        // Si la hora ya pasó hoy, programar para mañana
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback para Android 14+ si el permiso de alarma exacta no está concedido
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun parseTimeRobust(timeStr: String): Calendar? {
        val formats = listOf(
            SimpleDateFormat("hh:mm a", Locale.getDefault()),
            SimpleDateFormat("HH:mm", Locale.getDefault()),
            SimpleDateFormat("hh:mm a", Locale.US),
            SimpleDateFormat("h:mm a", Locale.getDefault())
        )
        
        var date: Date? = null
        for (sdf in formats) {
            try {
                date = sdf.parse(timeStr)
                if (date != null) break
            } catch (e: Exception) {}
        }
        
        if (date == null) return null

        val calendar = Calendar.getInstance()
        val timeCalendar = Calendar.getInstance().apply { time = date }
        
        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        return calendar
    }
}
