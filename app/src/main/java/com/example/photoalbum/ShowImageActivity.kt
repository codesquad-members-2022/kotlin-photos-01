package com.example.photoalbum

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.scale
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.load.engine.GlideException
import com.example.photoalbum.adapter.AdapterDoodle
import com.example.photoalbum.adapter.AdapterGridAlbum
import com.example.photoalbum.adapter.AdapterShowImage
import com.example.photoalbum.data.ShowImage
import com.example.photoalbum.databinding.ActivityShowImageBinding
import com.example.photoalbum.decoration.RecyclerDecoration
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ShowImageActivity : AppCompatActivity() {

    private val viewModel: ShowImageViewModel by viewModels()

    private lateinit var binding: ActivityShowImageBinding
    private lateinit var adapterShowImage: AdapterShowImage
    private lateinit var adapterDoodle: AdapterDoodle
    private lateinit var adapterGridAlbum: AdapterGridAlbum

    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1004

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_image)

        viewModel.initList()

        setGoToAlbumOpenBtn()
        setRecyclerView()

        viewModel.gridList.observe(this) {
            adapterGridAlbum.submitList(it)
        }

        setRecyclerView()
        CoroutineScope(Job() + Dispatchers.Main).launch {
            setImageView()
        }

        setToolbarMenuItemClickListener()

        observe()
        viewModel.getImage()

        setToolbarNavigationOnClickListener()

        setAlbumOpenBtn()
        setToolbarMenuListener()
    }

    private fun observe() {
        viewModel.imageDataList.observe(this) {
            Log.d("AppTest", "list observe")
            adapterDoodle.submitList(it.toMutableList())
            adapterDoodle.update()
        }
    }

    private fun setToolbarMenuItemClickListener() {
        binding.toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navi_goto_doodles -> {
                    Log.d("AppTest", "+ icon clicked")
                    // doodles 액티비티로 이동되게 구현하기
                    turnOffPhotoView()
                    turnOnDoodleView()
                    true
                }

                else -> false
            }
        }
    }

    private fun setToolbarNavigationOnClickListener() {
        binding.doodleToolBar.setNavigationOnClickListener {
            Log.d("AppTest", "setNavigationOnClickListener")
            turnOffDoodleView()
            turnOnPhotoView()
            setImageView()
        }
    }

    private fun setToolbarMenuListener() {
        binding.doodleToolBar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.navi_download -> {
                    val context = this
                    loadAndSave(context)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadAndSave(context: Context) =
        CoroutineScope(Job() + Dispatchers.Default).launch {
            kotlin.runCatching {
                val loadImageList = viewModel.selectImageLoad(context) ?: throw GlideException("stub")
                loadImageList.forEach { image ->
                    val saveState = viewModel.saveImage(image)
                }
            }.onSuccess {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(Runnable {
                    Toast.makeText(context, "저장이 완료되었습니다!", Toast.LENGTH_SHORT).show()
                }, 0)
            }.onFailure {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(Runnable {
                    Toast.makeText(context, "${it.message}저장이 실패하였습니다!", Toast.LENGTH_SHORT).show()
                }, 0)
            }
        }

    private fun setGoToAlbumOpenBtn() {
        binding.btnGoToAlbumOpen.setOnClickListener {
            Log.d("btncheck", "click!")
            turnOffColorRectangleView()
            turnOnOpenAlbumView()
        }
    }

    private fun setAlbumOpenBtn() {
        binding.btnOpenAlbum.setOnClickListener {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_READ_EXTERNAL_STORAGE)
        }
    }

    private fun setRecyclerView() {
        adapterGridAlbum = AdapterGridAlbum()
        binding.rvRandomRectangle.adapter = adapterGridAlbum
        binding.rvRandomRectangle.addItemDecoration(RecyclerDecoration(10, 0, 0, 20))
        binding.rvRandomRectangle.layoutManager = GridLayoutManager(this, 4)

        adapterShowImage = AdapterShowImage()
        binding.rvShowImage.adapter = adapterShowImage
        binding.rvShowImage.layoutManager = GridLayoutManager(this, 3)

        adapterDoodle = AdapterDoodle({
            viewModel.updateCheck(it)
            binding.doodleToolBar.menu.findItem(R.id.navi_download).isVisible = true
        }, {
            viewModel.changeCheckedState(it)
        })
        binding.doodleShowImage.adapter = adapterDoodle
        binding.doodleShowImage.layoutManager = GridLayoutManager(this, 3)
    }

    private fun setImageView() {
        val imageList = imagesLoad()
        val showImageList = mutableListOf<ShowImage>()
        for (i in 0 until imageList.size) {
            val bitmap = uriToBitmap(imageList[i] as String)
            bitmap.scale(100, 100)
            val showImage = ShowImage(i, bitmap)
            showImageList.add(showImage)
            Log.d("AppTest", imageList[i])
        }
        adapterShowImage.submitList(showImageList)
    }

    private fun uriToBitmap(image: String): Bitmap {
        return BitmapFactory.decodeFile(image)
    }

    private fun turnOnDoodleView() {
        binding.doodleToolBar.visibility = View.VISIBLE
        binding.doodleShowImage.visibility = View.VISIBLE
    }

    private fun turnOffPhotoView() {
        binding.rvShowImage.visibility = View.GONE
        binding.toolBar.visibility = View.GONE
    }

    private fun turnOffDoodleView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.doodleToolBar.visibility = View.GONE
        binding.doodleShowImage.visibility = View.GONE
    }

    private fun turnOnPhotoView() {
        binding.rvShowImage.visibility = View.VISIBLE
        binding.toolBar.visibility = View.VISIBLE
    }

    private fun turnOffAlbumOpen() {
        binding.ivAlbum.visibility = View.GONE
        binding.btnOpenAlbum.visibility = View.GONE
    }

    private fun turnOnOpenAlbumView() {
        binding.ivAlbum.visibility = View.VISIBLE
        binding.btnOpenAlbum.visibility = View.VISIBLE
    }

    private fun turnOffColorRectangleView() {
        binding.btnGoToAlbumOpen.visibility = View.GONE
        binding.viewDivider.visibility = View.GONE
        binding.rvRandomRectangle.visibility = View.GONE
    }

    private fun imagesLoad(): ArrayList<String> {
        val fileList = ArrayList<String>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)

        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            MediaStore.MediaColumns.DATE_ADDED + " desc"
        )

        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        val columnDisplayName = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        var lastIndex = 0

        while (cursor!!.moveToNext()) {
            Log.d("AppTest", "cursor")
            val absolutePathOfImage = cursor.getString(columnIndex!!)
            val nameOfFile = cursor.getString(columnDisplayName!!)

            lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile)
            if (lastIndex >= 0)
            else lastIndex = nameOfFile.length - 1

            if (!TextUtils.isEmpty(absolutePathOfImage)) {
                fileList.add(absolutePathOfImage)
                Log.d("AppTest", "isEmpty")
            }
        }

        Log.d("AppTest", "image List : ${fileList}")
        return fileList
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_READ_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("AppTest", "권한 승인")
                    Snackbar.make(binding.root, "권한이 승인되었습니다", Snackbar.LENGTH_SHORT).show()
                    turnOffAlbumOpen()
                    turnOnPhotoView()

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        Log.d("AppTest", "권한 최초 거절")
                        Snackbar.make(binding.root, "권한이 승인되지 않았습니다", Snackbar.LENGTH_SHORT).show()

                    } else {
                        Log.d("AppTest", "두 번째 거절")
                        Snackbar.make(binding.root, "'설정'에서 직접 권한 승인이 필요합니다", Snackbar.LENGTH_SHORT)
                            .show()

                        // '설정' 화면으로 이동하기
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null)
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }
        }
    }

}