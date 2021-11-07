package com.scheme.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scheme.utilities.EventTime
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.*

@Parcelize
@Entity(tableName = "event_table")
class DayEvent(
    var task: String,
    var day: Int,
    var start_hour: Int,
    var start_min: Int,
    var end_hour: Int,
    var end_min: Int,
    var color: Int,
    var auto: String,
    ) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @IgnoredOnParcel
    var id: Long = 0


    val beginning: EventTime
        get() = EventTime(
            start_hour,
            start_min
        )
    val ending: EventTime
        get() = EventTime(end_hour, end_min)

    val timeLeft: Long
        get() {
            val now = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"))
            val nowMillis = now.timeInMillis
            val nowHour = now[Calendar.HOUR_OF_DAY]
            val nowMin = now[Calendar.MINUTE]
            val today = now[Calendar.DAY_OF_WEEK]
            val thisTime = EventTime(start_hour, start_min)
            val hour = thisTime.hour
            val min = thisTime.minute
            val dayOfWeek: DayOfWeek = when (day) {
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

}