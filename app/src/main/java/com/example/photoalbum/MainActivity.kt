package com.example.photoalbum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photoalbum.adapter.AdapterGridAlbum
import com.example.photoalbum.databinding.ActivityMainBinding
import com.example.photoalbum.decoration.RecyclerDecoration

class MainActivity : AppCompatActivity() {

    private val viewModel : AlbumViewModel by viewModels()

    private lateinit var binding : ActivityMainBinding
    private lateinit var adapterGridAlbum: AdapterGridAlbum

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel.initList() // 리사이클러뷰에 사용할 리스트 초기화

        setBtn()
        setRecyclerView()
        viewModel.gridList.observe(this) {
            adapterGridAlbum.submitList(it)
        }
    }

    private fun setRecyclerView(){
            adapterGridAlbum = AdapterGridAlbum()
            binding.rvRandomRectangle.adapter = adapterGridAlbum
            binding.rvRandomRectangle.layoutManager = GridLayoutManager(this, 4)
            binding.rvRandomRectangle.addItemDecoration(RecyclerDecoration(30, 50, 0, 20))
    }

    private fun setBtn(){
        binding.btnGoToAlbumOpen.setOnClickListener {
            startActivity(Intent(this, AlbumOpenActivity::class.java))
        }
    }


}