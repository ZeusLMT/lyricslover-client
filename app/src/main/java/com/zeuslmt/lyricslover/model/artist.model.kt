package com.zeuslmt.lyricslover.model

data class Artist(
    val id: String,
    val name: String,
    val albums: Array<Album>,
    val songs: Array<Song>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artist

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}