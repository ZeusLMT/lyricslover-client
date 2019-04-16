package com.zeuslmt.lyricslover.model

import java.util.*

data class Song(
    val id: String,
    val title: String,
    val artist: Artist,
    val album: Album,
    val lyrics: String,
    val updatedAt: Date
)