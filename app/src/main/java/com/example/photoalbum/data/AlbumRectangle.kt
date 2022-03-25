package com.example.photoalbum.data

import android.graphics.Color
import kotlin.random.Random

data class AlbumRectangle(
    val id : Int,
    val color: Int =
        Color.rgb(
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256)
        )
)
