package com.nishant.questionnaire.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nishant.questionnaire.databinding.SingleAllTagsLayoutBinding

class FilterAdapter(
    val filterItemClicked: (String) -> Unit
) : ListAdapter<String, FilterAdapter.SingleEntry>(DiffUtil()) {

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    class SingleEntry(val binding: SingleAllTagsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: String) {
            binding.tag = tag
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleEntry {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SingleAllTagsLayoutBinding.inflate(inflater, parent, false)
        return SingleEntry(binding)
    }

    override fun onBindViewHolder(holder: SingleEntry, position: Int) {
        val currentTag = currentList[position]
        holder.bind(currentTag)
        holder.binding.filterCardView.setOnClickListener {
            filterItemClicked(currentTag)
        }
    }
}