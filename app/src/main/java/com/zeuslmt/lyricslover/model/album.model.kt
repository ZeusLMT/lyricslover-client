package com.zeuslmt.lyricslover.model

data class Album(
    val id: String,
    val title: String,
    val artist: Artist,
    val tracks: Array<Song>,
    val year: Int,
    val artwork: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Album

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}