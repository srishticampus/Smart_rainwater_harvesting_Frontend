package com.project.irhs.analysis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.irhs.R

class TableAdapter(private val data: List<TableData>) :
    RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slText: TextView = itemView.findViewById(R.id.slText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val hoursText: TextView = itemView.findViewById(R.id.hoursText)
        val precipitationText: TextView = itemView.findViewById(R.id.precipitationText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_table_row, parent, false)
        return TableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val item = data[position]
        holder.slText.text = item.sl.toString()
        holder.dateText.text = item.date
        holder.hoursText.text = item.hours
        holder.precipitationText.text = item.precipitation.toString()
    }

    override fun getItemCount(): Int = data.size
}