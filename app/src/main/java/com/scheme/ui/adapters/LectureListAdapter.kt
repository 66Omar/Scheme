package com.scheme.ui.adapters

import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.View.OnCreateContextMenuListener
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.scheme.R
import com.scheme.models.Lecture
import com.scheme.ui.adapters.LectureListAdapter.LectureViewHolder

class LectureListAdapter : RecyclerView.Adapter<LectureViewHolder>() {
    private var lectures: List<Lecture>? = null

    class LectureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        OnCreateContextMenuListener {
        var lecture: TextView = itemView.findViewById(R.id.lectureview)
        var doctor: TextView = itemView.findViewById(R.id.doctorview)
        var place: TextView = itemView.findViewById(R.id.placeview)
        var section: TextView = itemView.findViewById(R.id.sectionview)
        var day: TextView = itemView.findViewById(R.id.dayview)
        var time: TextView = itemView.findViewById(R.id.timeview)
        var time_left: TextView = itemView.findViewById(R.id.timeleftview)
        var lecturepackage: CardView = itemView.findViewById(R.id.lecturepackage)

        override fun onCreateContextMenu(menu: ContextMenu?, view: View?, p2: ContextMenuInfo?) {
                menu?.add(bindingAdapterPosition, 0, 0, "Delete")
                menu?.add(bindingAdapterPosition, 1, 1, "Replace")
                menu?.add(bindingAdapterPosition, 2, 2, "Replace (Different)")

        }

        init {
            lecturepackage.setOnCreateContextMenuListener(this)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tab1_item, parent, false)
        return LectureViewHolder(v)
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        val current_item = lectures!![position]
        holder.lecture.text = current_item.lecture
        holder.doctor.text = current_item.doctor
        holder.place.text = current_item.place
        holder.section.text = current_item.section
        holder.day.text = current_item.day
        holder.time.text = current_item.time
        holder.time_left.text = current_item.timeLeftString
    }

    override fun getItemCount(): Int {
        return if (lectures != null) {
            lectures!!.size
        } else 0
    }

    fun getItem(position: Int): Lecture? {
        return lectures?.get(position)
    }

    fun setList(newList: List<Lecture>?) {
        lectures = newList
        notifyDataSetChanged()
    }
}