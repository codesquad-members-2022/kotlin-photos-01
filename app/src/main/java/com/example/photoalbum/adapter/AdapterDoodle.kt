package com.example.photoalbum.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photoalbum.R
import com.example.photoalbum.data.ImageData
import com.example.photoalbum.databinding.ItemDoodleImageBinding
import com.example.photoalbum.util.DoodleDiffCallBack
import com.google.android.material.button.MaterialButton

class AdapterDoodle(
    private val itemLongClick: (index: Int) -> Unit,
    private val itemClick: (index: Int) -> Unit) : ListAdapter<ImageData, AdapterDoodle.MyViewHolder>(DoodleDiffCallBack) {

    class MyViewHolder(val binding: ItemDoodleImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageDataObject: ImageData, position: Int) {
            Glide.with(binding.root).load(imageDataObject.image).override(110, 50)
                .into(binding.ivDoodleImage)

            binding.doodleCheckBox.isChecked = false // 다른 곳 체크되지 않게 하기 위해 먼저 false 처리 해주기!!
            if(imageDataObject.checkBoxVisible) binding.doodleCheckBox.visibility = View.VISIBLE
            if(imageDataObject.selected) { // 체크박스의 버튼이미지? 를 빼니 코드로 check 동작
                binding.doodleCheckBox.isChecked = true
                Log.d("AppTest", "selected position : $position")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemDoodleImageBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_doodle_image, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), position)

        holder.binding.root.setOnLongClickListener {
            Log.d("AppTest", "item LongClicked")
            itemLongClick.invoke(position)
            true
        }

        holder.binding.root.setOnClickListener {
            if(getItem(position).checkBoxVisible){
                Log.d("AppTest", "item Clicked")
                itemClick.invoke(position)
            }
        }
        /*holder.binding.doodleCheckBox.setOnCheckedChangeListener{
            buttonView, isChecked -> itemClick.invoke(position)
        }*/
        // 체크박스를 선택하는 경우도 아이템 뷰를 클릭했을 때와 동일한 동작을 하게 구현했는데 앱이 터짐
        // 체크박스 클릭을 막는 방법을 선택
    }

    fun update(){
        notifyItemRangeChanged(0, itemCount)
    }

}