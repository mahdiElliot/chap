package com.example.chap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chap.R
import com.example.chap.model.Gift
import com.example.chap.view.fragment.DialogImageFragment
import com.example.chap.viewModel.GiftPromoFragmentViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_design.view.*
import kotlinx.android.synthetic.main.item_design.view.iv_cancel
import kotlinx.android.synthetic.main.item_time.view.*

class GiftPromoRecyclerViewAdapter(
    private val interaction: Interaction? = null,
    private val viewModel: GiftPromoFragmentViewModel,
    private val supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Gift>() {
        override fun areItemsTheSame(oldItem: Gift, newItem: Gift): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Gift, newItem: Gift): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GiftPromoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_design,
                parent,
                false
            ),
            interaction,
            viewModel,
            supportFragmentManager
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: ArrayList<Gift>) {
        differ.submitList(list)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GiftPromoViewHolder -> {
                holder.bind(differ.currentList[position], this)
            }
        }
    }


    class GiftPromoViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?,
        private val viewModel: GiftPromoFragmentViewModel,
        private val supportFragmentManager: FragmentManager
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Gift, adapter: GiftPromoRecyclerViewAdapter) = with(itemView) {
            setOnClickListener { radio_design.performClick() }
            setOnLongClickListener {
                DialogImageFragment().show(supportFragmentManager, item.url)
                true
            }

            radio_design.setOnClickListener {
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
                radio_design.isChecked = false
                iv_cancel.visibility = View.GONE
                adapter.differ.currentList[adapterPosition].isChosen = false
                viewModel.positionChecked.value = -1
                interaction?.onItemSelected(adapterPosition, item)
            }

            radio_design.isChecked = viewModel.positionChecked.value == adapterPosition

            iv_cancel.visibility = if (viewModel.positionChecked.value == adapterPosition)
                View.VISIBLE else View.GONE

            Picasso.get().load(item.url).into(iv_design)
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Gift)
    }

}