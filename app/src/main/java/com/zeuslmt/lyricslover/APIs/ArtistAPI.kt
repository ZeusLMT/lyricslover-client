package com.zeuslmt.lyricslover.APIs

import com.zeuslmt.lyricslover.models.Artist
import com.zeuslmt.lyricslover.models.NewArtist
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object ArtistAPI {
    //create URL
    const val url = "https://tuanl-sssf.jelastic.metropolia.fi/artist/"

    //Define services
    interface Service {
        @GET(".")
        fun getAllArtists() : Call<Array<Artist>>

        @FormUrlEncoded
        @POST(".")
        fun addNewArtist(
            @Field("name") artistName: String
        ): Call<NewArtist>
    }

    //Create and config Retrofit with builder
    private val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //create Retrofit service
    val service = retrofit.create(Service::class.java)
}