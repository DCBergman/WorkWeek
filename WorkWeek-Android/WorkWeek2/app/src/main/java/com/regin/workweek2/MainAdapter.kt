package com.regin.workweek2

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.regin.workweek2.models.NestedListModel
import com.regin.workweek2.models.UserLogDataModel
import com.regin.workweek2.network.Status
import com.regin.workweek2.network.UserLog
import java.time.LocalDate

class MainAdapter(
    private val context: Context,
    private val users: List<UserLogDataModel>,
    private var weeklyLogs: MutableList<Pair<Int?, List<UserLog>?>>,
    private var options: List<Status>,
    private var displayDate:LocalDate,
    private var viewModel: MainViewModel

) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private var personalWeeklyLogs: MutableList<UserLog> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.work_day_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

       var currentItem = users[position]
        if (users.isNotEmpty()) {
            if (position % 2 == 1) {
                holder.llWorkDay.setBackgroundColor(Color.parseColor("#D4E1D5"))
            } else {
                holder.llWorkDay.setBackgroundColor(Color.parseColor("#A7CBAB"))
            }
        }

        val isExpanded: Boolean = currentItem.expanded
        holder.clExpandable.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.llWorkDay.setOnClickListener {
            personalWeeklyLogs.clear()
            currentItem.expanded = !currentItem.expanded
            for(l in weeklyLogs){
                if(l.first == currentItem.id){
                    l.second?.let { list -> personalWeeklyLogs.addAll(list) }
                }
            }
            notifyItemChanged(position)
        }

        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        var tvWorkStatus: TextView = itemView.findViewById(R.id.tvWorkStatus)
        var llWorkDay: LinearLayout = itemView.findViewById(R.id.llWorkDay)
        var clExpandable: LinearLayout = itemView.findViewById(R.id.llExpandable)
        var rvNested: RecyclerView = itemView.findViewById(R.id.rvNested)
        var btnSave: Button = itemView.findViewById(R.id.btnSave)

        fun bind(position: Int) {
            tvUserName.text = "${users[position].firstName} ${users[position].lastName}"
            tvWorkStatus.text = users[position].workplaceStatus

            val adapter = NestedAdapter(
                personalWeeklyLogs,
                options,
                context,
                btnSave,
                users[position],
                displayDate,
                viewModel
            )

            rvNested.adapter = adapter
            rvNested.layoutManager = LinearLayoutManager(context)
            rvNested.setHasFixedSize(true)
        }
    }
}