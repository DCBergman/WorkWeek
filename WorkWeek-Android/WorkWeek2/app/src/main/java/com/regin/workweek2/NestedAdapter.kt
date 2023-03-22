package com.regin.workweek2

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.regin.workweek2.models.NestedListModel
import com.regin.workweek2.models.UserLogDataModel
import com.regin.workweek2.network.LogPost
import com.regin.workweek2.network.Status
import com.regin.workweek2.network.UserLog
import java.time.LocalDate


class NestedAdapter(
    private val weeklyLogs: MutableList<UserLog>,
    private val options: List<Status>,
    private val context: Context,
    private val btnSave: Button,
    private val currentUser: UserLogDataModel,
    private val displayDate: LocalDate,
    private val viewModel: MainViewModel

) : RecyclerView.Adapter<NestedAdapter.ViewHolder>() {

    private var optionStrings: MutableList<String> = mutableListOf()
    private var selection: Int = 0
    private var supportFuncs = SupportFuncs()
    private var logsToAdd: MutableList<LogPost?> = mutableListOf()
    private var weekDays = NestedListModel().weekDays


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.work_week_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("NestedAdapter", "Log1: $weeklyLogs")
        optionStrings.clear()
        optionStrings.addAll(options.map(Status::statusCode))

        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return weekDays.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var tvWeekDay: TextView = itemView.findViewById(R.id.tvWeekDay)
        var spinner: Spinner = itemView.findViewById(R.id.spinner)

        fun bind(position: Int) {

            val arrayAdapter: ArrayAdapter<String> =
                ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    optionStrings
                )

           val dailyLog: UserLog? = weeklyLogs.find { it.weekDayNumber - 1 == position }

            selection = if (dailyLog != null) {
                arrayAdapter.getPosition(dailyLog.statusCode)


            } else {
                -1
            }
            tvWeekDay.text = weekDays[position]
            spinner.adapter = arrayAdapter
            spinner.setSelection(selection)

            Log.d("NestedAdapter", "WeeklyLogs: $weeklyLogs")
            var log: LogPost? = null
//            Log.d("NestedAdapter", "WeeklyLogs ${weeklyLogs.toString()}")
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    p3: Long
                ) {

                    if (weeklyLogs.isNotEmpty()) {
                        log =
                            LogPost(
                                weeklyLogs[0].personId,
                                weeklyLogs[0].weekNumber,
                                adapterPosition + 1,
                                position
                            )
                    }else{
                        log =
                            LogPost(
                                currentUser.id,
                                supportFuncs.getDisplayWeekFromDate(displayDate),
                                adapterPosition + 1,
                                position
                            )
                    }


                    Log.d("NestedAdapter", "Log1: $logsToAdd")


                    if (log != null) {

                        if (logsToAdd.size > 0) {
                            if (logsToAdd.any { it -> it!!.weekDayNumber == log!!.weekDayNumber }) {
                                var replace =
                                    logsToAdd.find { it -> it!!.weekDayNumber == log!!.weekDayNumber }
                                Log.d("NestedAdapter", "Replace: $replace")
                                logsToAdd[logsToAdd.indexOf(replace)] = log

                            } else {
                                logsToAdd.add(log)
                            }
                        } else {
                            logsToAdd.add(log)
                        }

                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }


            btnSave.setOnClickListener {
           //     Log.d("NestedAdapter", "Log2: $logsToAdd")
                currentUser.expanded = false



                if (logsToAdd.isNotEmpty()) {
                    viewModel.postLogs(logsToAdd as List<LogPost>)
                    Toast.makeText(context, "Changes have been saved", LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "No logs to add", LENGTH_LONG).show()
                }
            }

        }
    }
}