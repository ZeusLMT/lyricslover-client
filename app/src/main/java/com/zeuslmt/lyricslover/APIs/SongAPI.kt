package com.zeuslmt.lyricslover.APIs

import com.zeuslmt.lyricslover.models.NewSong
import com.zeuslmt.lyricslover.models.Song
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object SongAPI {
    //create URL
    private const val url = "https://tuanl-sssf.jelastic.metropolia.fi/song/"

    //Define services
    interface Service {
        @GET(".")
        fun getAllSongs() : Call<Array<Song>>

        @GET("{song_id}")
        fun getSongById(@Path(value = "song_id", encoded = true) id: String) : Call<Song>

        @FormUrlEncoded
        @POST(".")
        fun addNewSong(
            @Field("title") title: String,
            @Field("artist") artistId: String,
            @Field("album") albumId: String,
            @Field("lyrics") lyrics: String
        ): Call<NewSong>

        @DELETE("{song_id}")
        fun deleteSongById(@Path(value = "song_id", encoded = true) id: String) : Call<ResponseBody>

        @FormUrlEncoded
        @PATCH("{song_id}")
        fun updateSong(
            @Path(value = "song_id", encoded = true) id: String,
            @Field("title") title: String,
            @Field("artist") artistId: String,
            @Field("album") albumId: String,
            @Field("lyrics") lyrics: String
        ): Call<NewSong>
    }

    //Create and config Retrofit with builder
    private val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //create Retrofit service
    val service = retrofit.create(Service::class.java)
}