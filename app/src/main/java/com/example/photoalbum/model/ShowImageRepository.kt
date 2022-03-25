package com.example.photoalbum.model

interface ShowImageRepository {
    suspend fun downloadJson(): String
}