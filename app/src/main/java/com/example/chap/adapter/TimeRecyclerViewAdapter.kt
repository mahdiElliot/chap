package com.example.chap.adapter

import android.content.res.TypedArray
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chap.R
import com.example.chap.model.DateTime
import com.example.chap.viewModel.CpPbFormFragmentViewModel
import kotlinx.android.synthetic.main.item_time.view.*

class TimeRecyclerViewAdapter(
    private val interaction: Interaction? = null,
    private val viewModel: CpPbFormFragmentViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            interaction,
            viewModel
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
        private val interaction: Interaction?,
        private val viewModel: CpPbFormFragmentViewModel
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: DateTime, adapter: TimeRecyclerViewAdapter) = with(itemView) {
            setOnClickListener { radio_time.performClick() }

            radio_time.setOnClickListener {
                for (i in 0 until adapter.differ.currentList.size)
                    if (i != adapterPosition && adapter.differ.currentList[i].isChosen) {
                        adapter.differ.currentList[i].isChosen = false
                        adapter.notifyItemChanged(i)
                    }

                adapter.differ.currentList[adapterPosition].isChosen = true
                viewModel.positionChecked.value = adapterPosition
                interaction?.onItemSelected(adapterPosition, item)
                iv_cancel.visibility = View.VISIBLE
            }

            iv_cancel.setOnClickListener {
                radio_time.isChecked = false
                iv_cancel.visibility = View.GONE
                adapter.differ.currentList[adapterPosition].isChosen = false
                viewModel.positionChecked.value = -1
                interaction?.onItemSelected(adapterPosition, item)
            }

            if (viewModel.switch.value == true)
                radio_time.setButtonDrawable(R.drawable.radiobutton_drawable)
            else
                radio_time.setButtonDrawable(R.drawable.radiobutton3_drawable)

            radio_time.isChecked = viewModel.positionChecked.value == adapterPosition

            iv_cancel.visibility = if (viewModel.positionChecked.value == adapterPosition)
                View.VISIBLE else View.GONE

            tv_date.text = item.date
            tv_time.text = item.hour
        }

    }

    interface Interaction {
        fun onItemSelected(position: Int, item: DateTime)
    }
}