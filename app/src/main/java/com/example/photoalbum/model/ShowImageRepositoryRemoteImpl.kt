package com.example.photoalbum.model

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class ShowImageRepositoryRemoteImpl: ShowImageRepository {
    override suspend fun downloadJson(): String {
        return withContext(Dispatchers.IO){
            val loadJson = URL("https://public.codesquad.kr/jk/doodle.json").openStream()
            val reader = BufferedReader(InputStreamReader(loadJson, "UTF-8"))
            val buffer = StringBuffer()
            var whileSwitch = true
            Log.d("AppTest", "downloading")
            while (whileSwitch) {
                val json = reader.readLine()
                if (json != null) {
                    whileSwitch = false
                }
                buffer.append(json)
                Log.d("AppTest", "${buffer}")
            }

            """$buffer""".trimIndent()
        }
    }
}