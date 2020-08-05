package com.example.chap.adapter

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chap.R
import com.example.chap.internal.DbAddressHelper
import com.example.chap.internal.OnError
import com.example.chap.model.Address
import com.example.chap.viewModel.AddressListFragmentViewModel
import kotlinx.android.synthetic.main.item_address.view.*

class AddressRecyclerViewAdapter(
    private val interaction: Interaction? = null,
    private val viewModel: AddressListFragmentViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Address>() {

        override fun areItemsTheSame(
            oldItem: Address,
            newItem: Address
        ): Boolean {
            return oldItem.lat == newItem.lat && oldItem.lng == newItem.lng
        }

        override fun areContentsTheSame(
            oldItem: Address,
            newItem: Address
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddressViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_address,
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

    fun submitList(list: ArrayList<Address>) {
        differ.submitList(list)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddressViewHolder -> {
                holder.bind(differ.currentList[position], this)
            }
        }
    }

    class AddressViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?,
        private val viewModel: AddressListFragmentViewModel
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Address, adapter: AddressRecyclerViewAdapter) = with(itemView) {
            setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            iv_edit_address.setOnClickListener {
                interaction?.onItemSelected(
                    adapterPosition,
                    item
                )
            }

            iv_delete.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("آیا از حذف اطمینان دارید ؟‌")
                builder.setCancelable(true)
                builder.setPositiveButton("بله") { _, _ ->
                    val cp = ArrayList<Address>()
                    cp.addAll(adapter.differ.currentList)
                    if (cp.remove(item)) {
                        if (viewModel.deleteAddress(item, object : OnError {
                                override fun onError(errMsg: String?) {
                                    Log.i("not deleted", errMsg!!)
                                }
                            })) {
                            adapter.submitList(cp)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
                builder.setNegativeButton("خیر") { dialog, _ -> dialog.cancel() }

                builder.create().show()
            }

            tv_address.text = item.address
        }

    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Address)
    }

}