package com.example.chap.adapter

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chap.R
import com.example.chap.internal.SolarCalendar
import com.example.chap.model.Comment
import kotlinx.android.synthetic.main.item_comment.view.*
import java.util.*
import kotlin.collections.ArrayList

class CommentsRecyclerViewAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comment>() {

        override fun areItemsTheSame(
            oldItem: Comment,
            newItem: Comment
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Comment,
            newItem: Comment
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_comment,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: ArrayList<Comment>) {
        differ.submitList(list)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommentViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    class CommentViewHolder
    constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Comment) = with(itemView) {

            tv_name.text = item.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tv_comment.text = Html.fromHtml(item.content, Html.FROM_HTML_MODE_COMPACT)
            } else {
                tv_comment.text = Html.fromHtml(item.content)
            }

            val date = item.date.split("-")
            val c = SolarCalendar(date[0].toInt(), date[1].toInt(), date[2].toInt())
            val y = c.jy
            val m = c.jm
            val d = c.jd
            tv_date.text = "$y/$m/$d"
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Comment)
    }


}