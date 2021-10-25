package com.scheme.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.scheme.utilities.EventTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.*

@Entity(tableName = "lecture_table")
class Lecture(
    @ColumnInfo(name = "lectureName")
    var lecture: String,
    var doctor: String,
    var place: String,
    var section: String,
    var day: String,
    var startHour: Int,
    var startMinute: Int,
    var endHour: Int,
    var endMinute: Int,
    var auto: Int
) {

    @PrimaryKey(autoGenerate = true)
    var id = 0

    @Ignore
    var day_value = 0


    val timeLeft: Long
        get() {
            val now = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"))
            val nowMillis = now.timeInMillis
            val nowHour = now[Calendar.HOUR_OF_DAY]
            val nowMin = now[Calendar.MINUTE]
            val today = now[Calendar.DAY_OF_WEEK]
            val day = day_value
            val thisTime =
                EventTime(startHour, startMinute)
            val hour = thisTime.hour
            val min = thisTime.minute
            val dayOfWeek: DayOfWeek
            dayOfWeek = when (day) {
                0 -> DayOfWeek.SATURDAY
                1 -> DayOfWeek.SUNDAY
                2 -> DayOfWeek.MONDAY
                3 -> DayOfWeek.TUESDAY
                4 -> DayOfWeek.WEDNESDAY
                5 -> DayOfWeek.THURSDAY
                6 -> DayOfWeek.FRIDAY
                else -> DayOfWeek.MONDAY
            }
            val eventday = exchange(dayOfWeek)
            var next = LocalDate.now().atTime(hour, min)
            if (eventday != today) {
                next = LocalDate.now().atTime(hour, min).with(TemporalAdjusters.next(dayOfWeek))
            } else {
                if (nowHour == hour) {
                    if (nowMin >= min) {
                        next = LocalDate.now().atTime(hour, min)
                            .with(TemporalAdjusters.next(dayOfWeek))
                    }
                } else {
                    if (nowHour > hour) {
                        next = LocalDate.now().atTime(hour, min)
                            .with(TemporalAdjusters.next(dayOfWeek))
                    }
                }
            }
            val zdt = next.atZone(ZoneId.of("Egypt"))
            return zdt.toInstant().toEpochMilli() - nowMillis
        }

    val timeLeftString: String
    get() {
        return if (timeLeft >= 86400000) {
            val time = (timeLeft / 86400000).toInt()
            time.toString() + "d"
        } else if (timeLeft >= 3600000) {
            val time = (timeLeft / 3600000).toInt()
            time.toString() + "h"
        } else {
            val time = (timeLeft / 60000).toInt()
            time.toString() + "m"
        }
    }

    val time: String
        get() = EventTime(startHour, startMinute).string

    companion object {
        private fun exchange(day: DayOfWeek): Int {
            if (day == DayOfWeek.FRIDAY) {
                return Calendar.FRIDAY
            }
            if (day == DayOfWeek.SATURDAY) {
                return Calendar.SATURDAY
            }
            if (day == DayOfWeek.SUNDAY) {
                return Calendar.SUNDAY
            }
            if (day == DayOfWeek.TUESDAY) {
                return Calendar.TUESDAY
            }
            if (day == DayOfWeek.MONDAY) {
                return Calendar.MONDAY
            }
            if (day == DayOfWeek.WEDNESDAY) {
                return Calendar.WEDNESDAY
            }
            return if (day == DayOfWeek.THURSDAY) {
                Calendar.THURSDAY
            } else -1
        }
    }

    init {
        when (day.lowercase()) {
            "saturday" -> day_value = 0
            "sunday" -> day_value = 1
            "monday" -> day_value = 2
            "tuesday" -> day_value = 3
            "wednesday" -> day_value = 4
            "thursday" -> day_value = 5
            "friday" -> day_value = 6
        }
    }
}