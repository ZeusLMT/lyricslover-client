package com.zeuslmt.lyricslover.models


data class Song(
    val _id: String,
    val title: String,
    val artist: Artist,
    val album: Album,
    val lyrics: String,
    val updatedAt: String
) {
    override fun toString(): String {
        return "Song(_id='$_id', title='$title', artist=$artist, album=$album, lyrics='$lyrics', updatedAt='$updatedAt')"
    }
}