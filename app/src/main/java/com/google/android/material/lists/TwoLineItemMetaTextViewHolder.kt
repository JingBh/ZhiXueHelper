package com.google.android.material.lists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import top.jingbh.zhixuehelper.databinding.MaterialListItemTwoLineMetaTextBinding

class TwoLineItemMetaTextViewHolder(
    binding: MaterialListItemTwoLineMetaTextBinding
) : ViewHolder(binding.root) {
    val root = binding.root

    val text = binding.mtrlListItemText
    val secondaryText = binding.mtrlListItemSecondaryText
    val metaText = binding.mtrlListItemMetaText

    companion object {
        fun create(parent: ViewGroup): TwoLineItemMetaTextViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = MaterialListItemTwoLineMetaTextBinding.inflate(inflater, parent, false)

            return TwoLineItemMetaTextViewHolder(binding)
        }
    }
}
