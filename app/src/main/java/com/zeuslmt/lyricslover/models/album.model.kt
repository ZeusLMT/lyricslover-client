package com.zeuslmt.lyricslover.models

data class Album(
    val _id: String,
    val title: String,
    val artist: Artist,
    val tracks: Array<Song>?,
    val year: Int?,
    val artwork: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Album

        if (_id != other._id) return false

        return true
    }

    override fun hashCode(): Int {
        return _id.hashCode()
    }
}