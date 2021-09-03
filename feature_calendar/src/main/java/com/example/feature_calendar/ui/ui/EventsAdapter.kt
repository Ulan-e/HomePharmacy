package com.example.feature_calendar.ui.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_calendar.R
import com.example.feature_calendar.databinding.ItemEventBinding
import com.example.feature_calendar.ui.data.CalendarEvent
import java.text.SimpleDateFormat
import java.util.*

class EventsAdapter(
        private val listener: Listener
) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd-M-yyyy hh:mm:ss")

    @SuppressLint("SimpleDateFormat")
    private val timeFormat = SimpleDateFormat("HH:mm")

    private var events: ArrayList<CalendarEvent> = arrayListOf()

    fun setData(data: ArrayList<CalendarEvent>) {
        events = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = events[position]
        holder.binding.medicineStatus.setOnClickListener {
            listener.onAcceptEvent(position, currentItem)
        }
        holder.binding.medicineName.text = currentItem.medicineName
        holder.binding.pillsCount.text = currentItem.medicineCount

        val time = timeFormat.format(currentItem.time)
        holder.binding.textTime.text = time

       /* if (currentItem.time - Date().time >= 0) {
            holder.binding.medicineStatus.text = "Пропущенно"
            holder.binding.medicineStatus.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.red))
        } else {*/
            holder.binding.medicineStatus.text = "Принять"
            holder.binding.medicineStatus.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.main_green))
                    //}
    }

    fun removeItem(position: Int) {
        events.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, events.size)
    }

    override fun getItemCount(): Int = events.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemEventBinding.bind(view)
    }

    interface Listener {
        fun onAcceptEvent(position: Int, event: CalendarEvent)
    }
}