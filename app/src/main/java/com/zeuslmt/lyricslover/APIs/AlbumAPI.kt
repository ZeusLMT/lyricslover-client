package com.zeuslmt.lyricslover.APIs

import android.media.Image
import com.zeuslmt.lyricslover.models.Album
import com.zeuslmt.lyricslover.models.NewAlbum
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object AlbumAPI {
    //create URL
    const val url = "https://tuanl-sssf.jelastic.metropolia.fi/album/"

    //Define services
    interface Service {
        @GET(".")
        fun getAllAlbums() : Call<Array<Album>>

        @GET("search")
        fun getAlbumsByArtist(@Query("artist") artistId: String) : Call<Array<Album>>

        @GET("{album_id}/artwork")
        fun getArtworkOfAlbum(@Path(value = "album_id", encoded = true) albumId: String) : Call<HashMap<String, String>>

        @Multipart
        @POST(".")
        fun addNewAlbum(
            @Part("title") albumTitle: RequestBody,
            @Part("artist") artistId: RequestBody,
            @Part("year") year: RequestBody?,
            @Part artwork: MultipartBody.Part?
        ): Call<NewAlbum>
    }

    //Create and config Retrofit with builder
    private val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //create Retrofit service
    val service = retrofit.create(Service::class.java)
}