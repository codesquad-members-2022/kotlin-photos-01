package com.example.photoalbum

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.photoalbum.data.ImageData
import com.example.photoalbum.model.ShowImageRepositoryRemoteImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.*
import android.media.MediaScannerConnection.MediaScannerConnectionClient as MediaScannerConnectionClient

class ShowImageViewModel: ViewModel() {

    private var imageList = listOf<ImageData>()
    private val _imageDataList = MutableLiveData<List<ImageData>>()
    var imageDataList : LiveData<List<ImageData>> = _imageDataList

    init {
        _imageDataList.value = imageList
    }

    private val showImageRepository = ShowImageRepositoryRemoteImpl()

    fun getImage(){
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

    fun updateCheck(selectedInx: Int){
        imageList.forEach {
            it.checkBoxVisible = true
        }
        imageList[selectedInx].selected = true

        _imageDataList.value = imageList
    }

    fun changeCheckedState(checkedInx: Int){
        imageList[checkedInx].selected = !imageList[checkedInx].selected
        _imageDataList.value = imageList
    }

    fun selectImageLoad(context: Context): List<File> {
        val fileList = mutableListOf<File>()
        val manager = Glide.with(context)
        Log.d("SaveTest", "next selectImageLoading")
        imageList.forEach {
            if(it.selected) {
                    val file = manager.downloadOnly().load(it.image).submit().get()
                    Log.d("SaveTest", "ImageDownLoading")
                    fileList.add(file)
                    Log.d("SaveTest", "ImageDown ${file.path}")
            }
        }
        return fileList
    }

    fun saveImage(context: Context, file: File) {
        val downloadPath = "/storage/emulated/0/Download"
        var localFile = File(downloadPath)
        if (!localFile.exists()) {
            localFile.mkdirs()
        }
        Log.d("SaveTest", "localFile ${localFile.path}")
        val filePath = downloadPath + "/" + System.currentTimeMillis() + ".jpeg"
        localFile = File(filePath)
        Log.d("SaveTest", "filePath ${filePath}")
        try {
            val fileInputStream = FileInputStream(file)
            val bufferedInputStream = BufferedInputStream(fileInputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()

            val imageByte = ByteArray(1024)
            var current = -1

            while (true) {
                current = bufferedInputStream.read()
                if(current == -1) {
                    break
                }
//                Log.d("SaveTest", "${current}")
                byteArrayOutputStream.write(current)
//                Log.d("SaveTest", "write1")
            }
            Log.d("SaveTest", "write2")
            val fileOutputStream = FileOutputStream(localFile)

            fileOutputStream.write(byteArrayOutputStream.toByteArray())

            Log.d("SaveTest", "write3")
            fileOutputStream.flush()
            fileOutputStream.close()
            fileInputStream.close()
//            val mediaScannerConnectionClient = MediaScannerConnectionClient() {
//
//            }

//            val mediaScannerConnection = MediaScannerConnection(context, mediaScannerConnectionClient)
//            mediaScannerConnection.connect()
            scanMedia(context, localFile)

        } catch (e : Exception) {
            e.printStackTrace()
        }

    }

    fun scanMedia(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(file)
        Log.d("SaveTest", "intent ${intent.data}")
        context.sendBroadcast(intent)
    }
}