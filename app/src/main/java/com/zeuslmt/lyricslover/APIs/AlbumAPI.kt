package com.zeuslmt.lyricslover.APIs

import com.zeuslmt.lyricslover.models.Album
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object AlbumAPI {
    //create URL
    const val url = "https://tuanl-sssf.jelastic.metropolia.fi/album/"

    //Define services
    interface Service {
        @GET(".")
        fun getAllAlbums() : Call<Array<Album>>
    }

    //Create and config Retrofit with builder
    private val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //create Retrofit service
    val service = retrofit.create(Service::class.java)
}