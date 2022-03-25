package com.example.photoalbum.data

data class ImageData(
    val title: String,
    val image: String,
    val date: String,
    var selected: Boolean,
    var checkBoxVisible: Boolean
)