package com.example.global_data.events

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import java.text.SimpleDateFormat
import java.util.*

data class CalendarEvent(
        val id: Int,
        val title: String,
        var description: String,
        val location: String,
        val startTime: Long,
        val endTime: Date,
        val status: Int,
        val timeZone: String
) {
    companion object {
        fun toContentValues(isWeek: Boolean, event: CalendarEvent): ContentValues {
            val freg = if(isWeek) "WEEKLY" else "DAILY"
            val cv = ContentValues()
            cv.put("calendar_id", 1)
            cv.put("title", event.title)
            cv.put("description", event.description)
            cv.put("eventLocation", event.location)
            cv.put("duration", "+P1H")
            cv.put("dtstart", event.startTime)
            cv.put("eventStatus", event.status)
            cv.put("eventTimezone", event.timeZone)
            cv.put("eventStatus", 0)
            cv.put("hasAlarm", 1)
            cv.put("rrule", "FREQ=$freg;UNTIL=" + getRule(event.endTime))
            return cv
        }

        fun toICSContentValues(event: CalendarEvent): ContentValues {
            val cv = ContentValues()
            cv.put(CalendarContract.Events.CALENDAR_ID, event.id)
            cv.put(CalendarContract.Events.TITLE, event.title)
            cv.put(CalendarContract.Events.DESCRIPTION, event.description)
            cv.put(CalendarContract.Events.EVENT_LOCATION, event.location)
            cv.put(CalendarContract.Events.DTSTART, event.startTime)
           // cv.put(CalendarContract.Events.DTEND, event.endTime)
            val cal: Calendar = Calendar.getInstance()
            val tz: TimeZone = cal.timeZone
            cv.put(CalendarContract.Events.EVENT_TIMEZONE, tz.displayName)
            /*
            cv.put(Events.STATUS, 1);
            cv.put(Events.VISIBLE, 0);
            cv.put("transparency", 0);

            return cv;
            */return cv
        }

        private fun getRule(untilDate: Date): String {
            val yyyyMMdd = SimpleDateFormat("yyyyMMdd")
            val dt = Calendar.getInstance()

            // Where untilDate is a date instance of your choice, for example 30/01/2012
            // Where untilDate is a date instance of your choice, for example 30/01/2012
            dt.time = untilDate

            // If you want the event until 30/01/2012, you must add one day from our day because UNTIL in RRule sets events before the last day
            // If you want the event until 30/01/2012, you must add one day from our day because UNTIL in RRule sets events before the last day
            dt.add(Calendar.DATE, 1)
            return yyyyMMdd.format(dt.time)
        }

        fun setReminder(event: CalendarEvent) : ContentValues{
            val reminder = ContentValues()
            reminder.put("event_id", event.id)
            reminder.put("minutes", 10)
            reminder.put("method", 1)
            return reminder
        }
    }
}