package com.example.chap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chap.R
import com.example.chap.model.DateTime
import kotlinx.android.synthetic.main.item_time.view.*

class TimeRecyclerViewAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DateTime>() {
        override fun areItemsTheSame(oldItem: DateTime, newItem: DateTime): Boolean {
            return oldItem.date == newItem.date && oldItem.hour == newItem.hour
        }

        override fun areContentsTheSame(oldItem: DateTime, newItem: DateTime): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TimeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_time,
                parent,
                false
            ),
            interaction
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: ArrayList<DateTime>) {
        differ.submitList(list)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TimeViewHolder -> {
                holder.bind(differ.currentList[position], this)
            }
        }
    }

    class TimeViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: DateTime, adapter: TimeRecyclerViewAdapter) = with(itemView) {
            setOnClickListener {
                for (i in 0 until adapter.differ.currentList.size)
                    if (i != adapterPosition && adapter.differ.currentList[i].isChosen) {
                        adapter.differ.currentList[i].isChosen = false
                        adapter.notifyItemChanged(i)
                    }
                adapter.differ.currentList[adapterPosition].isChosen = true
                radio_time.isChecked = true

                interaction?.onItemSelected(adapterPosition, item)
            }
            radio_time.setOnClickListener {
                for (i in 0 until adapter.differ.currentList.size)
                    if (i != adapterPosition && adapter.differ.currentList[i].isChosen) {
                        adapter.differ.currentList[i].isChosen = false
                        adapter.notifyItemChanged(i)
                    }
                adapter.differ.currentList[adapterPosition].isChosen = true
                interaction?.onItemSelected(adapterPosition, item)
            }

            radio_time.isChecked = item.isChosen

            tv_date.text = item.date
            tv_time.text = item.hour
        }

    }

    interface Interaction {
        fun onItemSelected(position: Int, item: DateTime)
    }
}