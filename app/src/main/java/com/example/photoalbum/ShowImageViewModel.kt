package com.example.photoalbum

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.photoalbum.data.AlbumRectangle
import com.example.photoalbum.data.ImageData
import com.example.photoalbum.model.ShowImageRepositoryRemoteImpl
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.*

class ShowImageViewModel : ViewModel() {

    private var imageList = listOf<ImageData>()
    private val _imageDataList = MutableLiveData<List<ImageData>>()
    var imageDataList: LiveData<List<ImageData>> = _imageDataList

    init {
        _imageDataList.value = imageList
    }

    private val showImageRepository = ShowImageRepositoryRemoteImpl()

    var gridList = MutableLiveData<List<AlbumRectangle>>()

    fun initList() {
        gridList.value = createAlbumRectangle()
    }

    private fun createAlbumRectangle(): List<AlbumRectangle> {
        val albumRectangleList = mutableListOf<AlbumRectangle>()
        for (i in 0 until 40) {
            albumRectangleList.add(AlbumRectangle(i))
        }
        return albumRectangleList
    }

    fun getImage() {
        viewModelScope.launch {
            val resultString = showImageRepository.downloadJson()
            imageList = jsonObjectList(resultString)
            _imageDataList.postValue(imageList)
        }
    }

    private fun jsonObjectList(jsonString: String): List<ImageData> {
        val jsonArray = JSONArray(jsonString)
        val jsonList = mutableListOf<ImageData>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            jsonList.add(
                ImageData(
                    jsonObject.getString("title"),
                    jsonObject.getString("image"),
                    jsonObject.getString("date"),
                    selected = false,
                    checkBoxVisible = false
                )
            )
        }
        return jsonList
    }

    fun updateCheck(selectedInx: Int) {
        imageList.forEach {
            it.checkBoxVisible = true
        }
        imageList[selectedInx].selected = true

        _imageDataList.value = imageList
    }

    fun changeCheckedState(checkedInx: Int) {
        imageList[checkedInx].selected = !imageList[checkedInx].selected
        _imageDataList.value = imageList
    }

    fun selectImageLoad(context: Context): List<File>? {
        val fileList = mutableListOf<File>()
        val manager = Glide.with(context)
        return kotlin.runCatching {
            imageList.forEach {
                if (it.selected) {
                    val file = manager.downloadOnly().load(it.image).submit().get()
                    fileList.add(file)
                }
            }
            return fileList
        }.getOrNull()
    }

    fun saveImage(file: File): Boolean {
        val downloadPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        var localFile = File(downloadPath.path)
        if (!localFile.exists()) {
            localFile.mkdirs()
        }

        val filePath = downloadPath.path + "/" + System.currentTimeMillis() + ".jpeg"
        localFile = File(filePath)
        kotlin.runCatching {
            val fileInputStream = FileInputStream(file)
            val bufferedInputStream = BufferedInputStream(fileInputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()

            var current = -1

            while (true) {
                current = bufferedInputStream.read()
                if (current == -1) {
                    break
                }
                byteArrayOutputStream.write(current)
            }
            val fileOutputStream = FileOutputStream(localFile)

            fileOutputStream.write(byteArrayOutputStream.toByteArray())

            fileOutputStream.flush()
            fileOutputStream.close()
            fileInputStream.close()

            return true
        }.getOrNull()

        return false
    }

}

