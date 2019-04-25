package com.zeuslmt.lyricslover.models

data class Artist(
    val _id: String,
    val name: String,
    val albums: Array<Album>?,
    val songs: Array<Song>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artist

        if (_id != other._id) return false

        return true
    }

    override fun hashCode(): Int {
        return _id.hashCode()
    }
}

data class NewArtist (val _id: String,
                      val name: String)