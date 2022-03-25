package com.example.photoalbum.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.R
import com.example.photoalbum.data.ShowImage
import com.example.photoalbum.databinding.ItemShowImageBinding
import com.example.photoalbum.util.ShowImageDiffCallBack

class AdapterShowImage : ListAdapter<ShowImage, AdapterShowImage.MyViewHolder>(ShowImageDiffCallBack) {

    class MyViewHolder(private val binding: ItemShowImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(showImage: ShowImage) {
            binding.ivShowImage.setImageBitmap(showImage.bitmap)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemShowImageBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_show_image, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}