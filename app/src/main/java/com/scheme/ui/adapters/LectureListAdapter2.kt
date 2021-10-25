package com.scheme.ui.adapters

import android.view.*
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.scheme.R
import com.scheme.models.Lecture

class LectureListAdapter2() : RecyclerView.Adapter<LectureListAdapter2.LectureViewHolder2>() {
    private var Lectures: List<Lecture>? = null
    val itemClicked = MutableLiveData<Lecture>()


    class LectureViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView)
         {
        var lecture: TextView = itemView.findViewById(R.id.lectureview)
        var doctor: TextView = itemView.findViewById(R.id.doctorview)
        var place: TextView = itemView.findViewById(R.id.placeview)
        var section: TextView = itemView.findViewById(R.id.sectionview)
        var day: TextView = itemView.findViewById(R.id.dayview)
        var time: TextView = itemView.findViewById(R.id.timeview)
        var time_left: TextView = itemView.findViewById(R.id.timeleftview)
        var lecturepackage: CardView = itemView.findViewById(R.id.lecturepackage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder2 {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tab1_item, parent, false)
        return LectureViewHolder2(v)
    }

    override fun onBindViewHolder(holder: LectureViewHolder2, position: Int) {
        val current_item = Lectures!![position]
        holder.lecture.text = current_item.lecture
        holder.doctor.text = current_item.doctor
        holder.place.text = current_item.place
        holder.section.text = current_item.section
        holder.day.text = current_item.day
        holder.time.text = current_item.time
        holder.time_left.text = current_item.timeLeftString
        holder.lecturepackage.setOnClickListener { itemClicked.value = current_item }

    }

    override fun getItemCount(): Int {
        return if (Lectures != null) {
            Lectures!!.size
        } else 0
    }

    fun getItem(position: Int): Lecture? {
        return Lectures?.get(position)
    }

    fun setList(newList: List<Lecture>?) {
        Lectures = newList
        notifyDataSetChanged()
    }
}