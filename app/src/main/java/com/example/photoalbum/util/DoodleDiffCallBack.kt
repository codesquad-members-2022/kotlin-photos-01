package com.example.photoalbum.util

import androidx.recyclerview.widget.DiffUtil
import com.example.photoalbum.data.ImageData

object DoodleDiffCallBack: DiffUtil.ItemCallback<ImageData>() {
    override fun areItemsTheSame(
        oldItem: ImageData,
        newItem: ImageData
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: ImageData,
        newItem: ImageData
    ): Boolean {
        return oldItem == newItem
    }

}