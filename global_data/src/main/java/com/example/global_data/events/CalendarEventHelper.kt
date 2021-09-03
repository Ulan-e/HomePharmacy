package com.example.global_data.events

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.util.Log

class CalendarEventHelper(private val context: Context) {

    private var baseUri: String? = null

    fun setURI() {
        baseUri = getCalendarUriBase()
    }

    fun addEvent(isWeek: Boolean, event: CalendarEvent?) {
        try {
            val evtUri: Uri? =
                    getCalendarUri("events")?.let {
                        context.contentResolver.insert(it, event?.let { it1 -> CalendarEvent.toContentValues(isWeek, it1) })
                    }
            //val rmdUri : Uri? = getCalendarUri("reminders")?.let { context.contentResolver.insert(it, event?.let { it2 ->CalendarEvent.setReminder(it2) }) }
            Log.d("ulancal", "event URI $evtUri")
            //Log.d("ulancal", "reminder URI $rmdUri")
        } catch (t: Throwable) {
            Log.e("ulancal", "event Error", t);
        }
    }

    fun addEventICS(event: CalendarEvent?) {
        val cr: ContentResolver = context.contentResolver
        val uri = cr.insert(Events.CONTENT_URI, event?.let { CalendarEvent.toICSContentValues(it) })
        println("Event URI [$uri]")
    }


    private fun getCalendarUri(path: String): Uri? {
        return Uri.parse(baseUri.toString() + "/" + path)
    }

    private fun getCalendarUriBase(): String? {
        var calendarUriBase: String? = null
        var calendars: Uri = Uri.parse("content://calendar/calendars")
        var managedCursor: Cursor? = null
        try {
            managedCursor = context.contentResolver.query(calendars, null, null, null, null)
        } catch (e: Exception) {
            Log.e("ulancal", "context.contentResolver.query Error", e);
        }
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/"
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars")
            try {
                managedCursor = context.contentResolver.query(calendars, null, null, null, null)
            } catch (e: Exception) {
                Log.e("ulancal", "context.contentResolver.query 222 Error", e);
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/"
            }
        }
        Log.d("ulancal", "URI [$calendarUriBase]")
        return calendarUriBase
    }


}