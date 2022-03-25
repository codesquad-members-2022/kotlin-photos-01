package com.example.photoalbum.util

import androidx.recyclerview.widget.DiffUtil
import com.example.photoalbum.data.AlbumRectangle
import com.example.photoalbum.data.ShowImage

object ShowImageDiffCallBack: DiffUtil.ItemCallback<ShowImage>() {
    override fun areItemsTheSame(
        oldItem: ShowImage,
        newItem: ShowImage
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ShowImage,
        newItem: ShowImage
    ): Boolean {
        return oldItem == newItem
    }

}