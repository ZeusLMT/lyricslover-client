package com.zeuslmt.lyricslover.APIs

import com.zeuslmt.lyricslover.models.Artist
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object ArtistAPI {
    //create URL
    const val url = "http://192.168.31.147:3000/artist/"

    //Define services
    interface Service {
        @GET(".")
        fun getAllArtists() : Call<Array<Artist>>
    }

    //Create and config Retrofit with builder
    private val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //create Retrofit service
    val service = retrofit.create(Service::class.java)
}