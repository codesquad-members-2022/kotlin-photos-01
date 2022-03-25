package com.example.photoalbum.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.R
import com.example.photoalbum.data.AlbumRectangle
import com.example.photoalbum.databinding.ItemGridAlbumBinding
import com.example.photoalbum.util.MyDiffCallBack

class AdapterGridAlbum : ListAdapter<AlbumRectangle, AdapterGridAlbum.MyViewHolder>(MyDiffCallBack) {

    class MyViewHolder(private val binding: ItemGridAlbumBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(albumRectangle: AlbumRectangle) {
            binding.root.setBackgroundColor(albumRectangle.color)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemGridAlbumBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_grid_album, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}