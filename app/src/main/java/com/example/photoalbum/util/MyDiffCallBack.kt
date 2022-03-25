package com.example.photoalbum.util

import androidx.recyclerview.widget.DiffUtil
import com.example.photoalbum.data.AlbumRectangle

object MyDiffCallBack: DiffUtil.ItemCallback<AlbumRectangle>() {
    override fun areItemsTheSame(
        oldItem: AlbumRectangle,
        newItem: AlbumRectangle
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: AlbumRectangle,
        newItem: AlbumRectangle
    ): Boolean {
        return oldItem == newItem
    }

}