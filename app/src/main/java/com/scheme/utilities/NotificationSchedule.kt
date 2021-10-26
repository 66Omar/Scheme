package com.scheme.utilities

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.scheme.App
import com.scheme.MainActivity
import com.scheme.R
import com.scheme.models.DayEvent
import com.scheme.models.Lecture
import java.util.ArrayList

object NotificationSchedule  {
    private const val firstReminderAdvance = 1800000  // 30 minutes
    private const val secondReminderAdvance = 900000  // 15 minutes

    fun scheduleLectureNotifications(context: Context, lectures: List<Lecture>) {
        val ids = ArrayList<Int>()

        for (item in lectures) {
            for (i in 0..2) {
                val id = item.id + secondReminderAdvance * i
                var timeLeft = item.timeLeft

                if (i == 1) {
                    timeLeft -= firstReminderAdvance.toLong()
                }
                if (i == 2) {
                    timeLeft -= secondReminderAdvance.toLong()
                }
                if (timeLeft >= 0) {
                    val notificationIntent =
                        Intent(context, NotificationSender::class.java)
                    notificationIntent.putExtra(NotificationSender.NOTIFICATION_ID, id)
                    notificationIntent.putExtra(
                        NotificationSender.NOTIFICATION,
                        createLectureNotification(context, item, i, System.currentTimeMillis() + timeLeft)
                    )

                    val pendingIntent =
                        PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    val alarmManager =
                        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeLeft] =
                        pendingIntent
                    ids.add(id)
                }
            }
        }
        AlarmUtils.saveIdsInPreferences(context, ids)
    }


    fun scheduleEventNotifications(context: Context, events: List<DayEvent>) {
        val ids = ArrayList<Int>()

        for (item in events) {
            if (item.auto == "user") {
                for (i in 0..2) {
                    val id = item.id + secondReminderAdvance * i
                    var timeLeft = item.timeLeft
                    if (i == 1) {
                        timeLeft -= firstReminderAdvance.toLong()
                    }
                    if (i == 2) {
                        timeLeft -= secondReminderAdvance.toLong()
                    }
                    if (timeLeft >= 0) {
                        val notificationIntent =
                            Intent(context, NotificationSender::class.java)
                        notificationIntent.putExtra(NotificationSender.NOTIFICATION_ID, id)
                        notificationIntent.putExtra(
                            NotificationSender.NOTIFICATION,
                            createEventNotification(
                                context,
                                item,
                                i,
                                System.currentTimeMillis() + timeLeft
                            )
                        )
                        val pendingIntent =
                            PendingIntent.getBroadcast(
                                context,
                                id,
                                notificationIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        val alarmManager =
                            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeLeft] =
                            pendingIntent
                        ids.add(id)
                    }
                }
            }
        }
        AlarmUtils.saveIdsInPreferences(context, ids)
    }


    private fun createLectureNotification(
        context: Context,
        lecture: Lecture,
        state: Int,
        time: Long
    ): Notification {
        val builder = NotificationCompat.Builder(context, App.CHANNEL_ONE)
        val pendingIntent = PendingIntent.getActivity(
            context,
            lecture.id,
            Intent(context, MainActivity::class.java),
            0
        )
        builder.setContentIntent(pendingIntent)
        if (state == 0) {
            builder.setContentTitle("Lecture Started")
        } else {
            builder.setContentTitle("Upcoming Lecture")
        }
        builder.setDefaults(Notification.DEFAULT_ALL)
        var message = ""
        if (state == 0) {
            message =
                "Lecture ${lecture.lecture} by doctor ${lecture.doctor} has started!\nLocation: ${lecture.place}"
        }
        if (state == 1) {
            message =
                "Lecture ${lecture.lecture} by doctor ${lecture.doctor} starts in 30 minutes!\nLocation: ${lecture.place}"
        }
        if (state == 2) {
            message =
                "Lecture ${lecture.lecture} by doctor ${lecture.doctor} starts  in 15 minutes!\nLocation: ${lecture.place}"
        }
        builder.setWhen(time)
        builder.setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(message)
        )
        builder.setContentText(message)
        builder.setSmallIcon(R.mipmap.ic_launcher_foreground)
        builder.setAutoCancel(true)
        builder.priority = Notification.PRIORITY_MAX
        return builder.build()
    }

    private fun createEventNotification(
        context: Context,
        event: DayEvent,
        state: Int,
        time: Long
    ): Notification {
        val builder = NotificationCompat.Builder(context, App.CHANNEL_TWO)
        val pendingIntent = PendingIntent.getActivity(
            context,
            event.id,
            Intent(context, MainActivity::class.java),
            0
        )
        builder.setContentIntent(pendingIntent)
        if (state == 0) {
            builder.setContentTitle("An event has just started")
        } else {
            builder.setContentTitle("Upcoming event")
        }
        builder.setDefaults(Notification.DEFAULT_ALL)
        var message = ""
        if (state == 0) {
            message = "Event ${event.task} is happening now\nStarted at: ${event.beginning.string}"
        }
        if (state == 1) {
            message = "You have an upcoming event: ${event.task}\nStarts at: ${event.beginning.string}"

        }
        if (state == 2) {
            message = "You have an upcoming event: ${event.task}\nStarts at: ${event.beginning.string}"
        }
        builder.setWhen(time)
        builder.setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(message)
        )
        builder.setContentText(message)

        builder.setSmallIcon(R.mipmap.ic_launcher_foreground)
        builder.setAutoCancel(true)
        builder.priority = Notification.PRIORITY_MAX
        return builder.build()
    }
}
