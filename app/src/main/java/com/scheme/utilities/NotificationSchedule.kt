package com.scheme.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.scheme.App
import com.scheme.models.DayEvent
import com.scheme.models.Lecture
import com.scheme.utilities.SchemeUtils.getNotificationIds
import java.util.ArrayList

object NotificationSchedule {

    private const val FIRST_REMINDER_ADVANCE = 3600000  // 1 hour
    private const val SECOND_REMINDER_ADVANCE = 900000  // 15 minutes
    private const val LECTURE_TYPE = 0
    private const val EVENT_TYPE = 1

    fun scheduleLecturesNotifications(context: Context, lectures: List<Lecture>) {
        val ids = ArrayList<Int>()
        for (lecture in lectures) {
            val generatedIds = getNotificationIds(lecture.id, LECTURE_TYPE)
            for (i in 0..2) {
                lectureNotification(context, generatedIds[i], i, lecture)
                ids.add(generatedIds[i])
            }
        }
        AlarmUtils.saveIdsInPreferences(context, ids, LECTURE_TYPE)
    }


    fun scheduleLectureNotifications(context: Context, lecture: Lecture) {
        val generatedIds = getNotificationIds(lecture.id, LECTURE_TYPE)
        for (i in 0..2) {
            lectureNotification(context, generatedIds[i], i, lecture)
            AlarmUtils.saveAlarmId(context, generatedIds[i], LECTURE_TYPE)
        }
    }

    fun scheduleEventNotification(context: Context, event: DayEvent) {
        if (event.auto == "user") {
            val generatedIds = getNotificationIds(event.id, EVENT_TYPE)
            for (i in 0..2) {
                eventNotification(context, generatedIds[i], i, event)
                AlarmUtils.saveAlarmId(context, generatedIds[i], EVENT_TYPE)
            }
        }
    }

    fun cancelAllLectureNotifications(context: Context) {
        AlarmUtils.cancelAllAlarms(context, Intent(context, NotificationSender::class.java), 0)
    }

    fun cancelLectureNotifications(context: Context, lecture: Lecture) {
        for (each in getNotificationIds(lecture.id, LECTURE_TYPE)) {
            AlarmUtils.cancelAlarm(
                context,
                Intent(context, NotificationSender::class.java),
                each,
                LECTURE_TYPE
            )
        }
    }

    fun cancelEventNotifications(context: Context, event: DayEvent) {
        for (each in getNotificationIds(event.id, EVENT_TYPE)) {
            AlarmUtils.cancelAlarm(
                context,
                Intent(context, NotificationSender::class.java),
                each,
                EVENT_TYPE
            )
        }
    }


    private fun lectureNotification(
        context: Context,
        notificationId: Int,
        state: Int,
        lecture: Lecture
    ) {
        var timeLeft = lecture.timeLeft
        var title = "Lecture Started"
        var message =
            "Lecture ${lecture.lecture} by doctor ${lecture.doctor} has started!\nLocation: ${lecture.place}"

        if (state == 1) {
            timeLeft -= FIRST_REMINDER_ADVANCE.toLong()
            title = "Upcoming Lecture"
            message =
                "Lecture ${lecture.lecture} by doctor ${lecture.doctor} starts in an hour!\nLocation: ${lecture.place}"
        }
        if (state == 2) {
            timeLeft -= SECOND_REMINDER_ADVANCE.toLong()
            title = "Upcoming Lecture"
            message =
                "Lecture ${lecture.lecture} by doctor ${lecture.doctor} starts in 15 minutes!\nLocation: ${lecture.place}"
        }
        if (timeLeft >= 0) {
            val notificationIntent =
                Intent(context, NotificationSender::class.java)
            notificationIntent.putExtra(NotificationSender.NOTIFICATION_ID, notificationId)
            notificationIntent.putExtra(NotificationSender.NOTIFICATION_CONTENT, message)
            notificationIntent.putExtra(NotificationSender.NOTIFICATION_TITLE, title)
            notificationIntent.putExtra(
                NotificationSender.NOTIFICATION_CHANNEL,
                App.CHANNEL_ONE
            )

            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + timeLeft,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + timeLeft,
                    pendingIntent
                )
            }
        }
    }

    private fun eventNotification(
        context: Context,
        notificationId: Int,
        state: Int,
        event: DayEvent
    ) {
        var timeLeft = event.timeLeft
        var title = "An event has just started"
        var message =
            "Event ${event.task} is happening now\nStarted at: ${event.beginning.string}"


        if (state == 1) {
            timeLeft -= FIRST_REMINDER_ADVANCE.toLong()
            title = "Upcoming event"
            message =
                "You have an upcoming event: ${event.task}\nStarts at: ${event.beginning.string}"
        }
        if (state == 2) {
            timeLeft -= SECOND_REMINDER_ADVANCE.toLong()
            title = "Upcoming event"
            message =
                "You have an upcoming event: ${event.task}\nStarts at: ${event.beginning.string}"
        }
        if (timeLeft >= 0) {
            val notificationIntent =
                Intent(context, NotificationSender::class.java)
            notificationIntent.putExtra(NotificationSender.NOTIFICATION_ID, notificationId)
            notificationIntent.putExtra(NotificationSender.NOTIFICATION_CONTENT, message)
            notificationIntent.putExtra(NotificationSender.NOTIFICATION_TITLE, title)
            notificationIntent.putExtra(
                NotificationSender.NOTIFICATION_CHANNEL,
                App.CHANNEL_TWO
            )

            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + timeLeft,
                pendingIntent
            )
        }
    }
}
