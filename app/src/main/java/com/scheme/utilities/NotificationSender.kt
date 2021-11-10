package com.scheme.utilities

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import com.scheme.App
import com.scheme.MainActivity
import com.scheme.R
import com.scheme.data.EventRepository
import com.scheme.data.LectureRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationSender : BroadcastReceiver() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    @Inject
    lateinit var lectureRepository: LectureRepository
    @Inject
    lateinit var eventRepository: EventRepository
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            reSchedule(context)
        }
        else {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val id = intent.getIntExtra(NOTIFICATION_ID, 0)
            val content = intent.getStringExtra(NOTIFICATION_CONTENT)
            val title = intent.getStringExtra(NOTIFICATION_TITLE)
            val channel = intent.getStringExtra(NOTIFICATION_CHANNEL)
            val notification = createNotification(context, title, content, channel)
            notificationManager.notify(id, notification)

            // scheduling next alarm
            val notificationIntent = Intent(context, NotificationSender::class.java)
            notificationIntent.putExtra(NOTIFICATION_ID, id)
            notificationIntent.putExtra(NOTIFICATION_CONTENT, content)
            notificationIntent.putExtra(NOTIFICATION_TITLE, title)
            notificationIntent.putExtra(NOTIFICATION_CHANNEL, channel)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val p = PendingIntent.getBroadcast(
                context,
                id,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ONE_WEEK, p)
        }
    }

    private fun createNotification(
        context: Context,
        title: String?,
        message: String?,
        channel: String?
    ): Notification {
        val pendingIntent =
            PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)
        val builder = NotificationCompat.Builder(context, channel!!)
        builder.setContentTitle(title)
            .setContentText(message)
            .setWhen(System.currentTimeMillis())
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
        return builder.build()
    }

    private fun reSchedule(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(App.SHARED_PREFS, Context.MODE_PRIVATE)
        val section = sharedPreferences.getString(App.SECTION, null)
        if (!section.isNullOrEmpty()) {
            scope.launch {
                val lectures = lectureRepository.getAllAsList(section)
                NotificationSchedule.scheduleLecturesNotifications(context, lectures)
                val events = eventRepository.getAllAsList()
                for (event in events) {
                    NotificationSchedule.scheduleEventNotification(context, event)
                }
            }

        }
    }

    companion object {
        const val NOTIFICATION_ID = "notification_id"
        const val NOTIFICATION_CONTENT = "notification_content"
        const val NOTIFICATION_TITLE = "notification_title"
        const val NOTIFICATION_CHANNEL = "notification_channel"
        private const val ONE_WEEK = 604800000
    }
}