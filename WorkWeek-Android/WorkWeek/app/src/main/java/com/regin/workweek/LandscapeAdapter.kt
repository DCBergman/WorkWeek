package com.regin.workweek

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.regin.workweek.models.UserLogDataModel
import com.regin.workweek.network.UserLog

class LandscapeAdapter(
    private val users: List<UserLogDataModel>,
    private val weeklyLogs: MutableList<Pair<Int?, List<UserLog>?>>,
    private val context: Context
): RecyclerView.Adapter<LandscapeAdapter.ViewHolder>() {

    private var name: String = ""
    var personalWeeklyLogs:MutableList<UserLog> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandscapeAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.landscape_work_week_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentItem = users[position]

        personalWeeklyLogs.clear()


       name = "${currentItem.firstName} ${currentItem.lastName[0]}"
        if (weeklyLogs.isNotEmpty()) {
            if (position % 2 == 1) {
                holder.trWeekStatus.setBackgroundColor(Color.parseColor("#A7CBAB"))
            } else {
                holder.trWeekStatus.setBackgroundColor(Color.parseColor("#D4E1D5"))
            }
        }

        for(l in weeklyLogs){
            if(l.first == currentItem.id){
                l.second?.let { it -> personalWeeklyLogs.addAll(it) }
            }
        }

        Log.d("LandscapeAdapter", "Personal wl: $personalWeeklyLogs")

        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var trWeekStatus: TableRow = itemView.findViewById(R.id.trWeekStatus)
        var tvName: TextView = itemView.findViewById(R.id.tvTableName)
        var tvWeekday1: TextView = itemView.findViewById(R.id.tvWeekday1)
        var tvWeekday2: TextView = itemView.findViewById(R.id.tvWeekday2)
        var tvWeekday3: TextView = itemView.findViewById(R.id.tvWeekday3)
        var tvWeekday4: TextView = itemView.findViewById(R.id.tvWeekday4)
        var tvWeekday5: TextView = itemView.findViewById(R.id.tvWeekday5)

        fun bind(position: Int) {
            tvName.text = name
            tvWeekday1.text = personalWeeklyLogs.find { it.weekDayNumber==1 }?.statusCode ?: ""
            tvWeekday2.text = personalWeeklyLogs.find { it.weekDayNumber==2 }?.statusCode ?: ""
            tvWeekday3.text = personalWeeklyLogs.find { it.weekDayNumber==3 }?.statusCode ?: ""
            tvWeekday4.text = personalWeeklyLogs.find { it.weekDayNumber==4 }?.statusCode ?: ""
            tvWeekday5.text = personalWeeklyLogs.find { it.weekDayNumber==5 }?.statusCode ?: ""
        }

    }



}