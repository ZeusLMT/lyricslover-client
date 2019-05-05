package com.zeuslmt.lyricslover.SongDetailsActivity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import com.zeuslmt.lyricslover.APIs.AlbumAPI
import com.zeuslmt.lyricslover.APIs.SongAPI
import com.zeuslmt.lyricslover.NewSongActivity.NewSongActivity
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Song
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_song_details.*
import kotlinx.android.synthetic.main.fragment_songs.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class SongDetailsActivity : AppCompatActivity() {
    private lateinit var songId: String
    private lateinit var song: Song

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_details)
        textView_lyrics.movementMethod = ScrollingMovementMethod()

        songId = intent.getStringExtra("_id")

        btn_edit.setOnClickListener {
            val intent = Intent(this, NewSongActivity::class.java).apply {}
            intent.putExtra("_id", songId)
            intent.putExtra("editMode", true)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (songId.isNotEmpty()) {
            getSong(songId)
        }
    }

    private fun getSong(songId: String) {
        val songService = SongAPI.service

        val result = object : Callback<Song> {
            override fun onFailure(call: Call<Song>, t: Throwable) {
                Log.d("SongService", "Error: $t")
            }

            override fun onResponse(call: Call<Song>?, response: Response<Song>?) {
                if (response != null) {
                    song = response.body()!!
                    Log.d("SongService", song.toString())

                    textView_songTitle.text = song.title
                    textView_artistName.text = (song.artist).name
                    textView_updatedAt.text = getString(R.string.textView_updatedAt, song.updatedAt)
                    textView_lyrics.text = song.lyrics

                    if (song.album != null) {
                        textView_albumTitle.visibility = View.VISIBLE
                        album_artist_divider.visibility = View.VISIBLE
                        textView_albumTitle.text = (song.album)!!.title
                        getArtworkFromAlbum(song.album!!._id)
                    } else {
                        textView_albumTitle.visibility = View.GONE
                        album_artist_divider.visibility = View.GONE
                        Blurry.with(applicationContext)
                            .radius(5)
                            .sampling(5)
                            .color(Color.argb(90, 255, 255, 255))
                            .from(BitmapFactory.decodeResource(resources, R.drawable.song_bg))
                            .into(imageView_bg)
                    }
                }
            }
        }
        songService.getSongById(songId).enqueue(result)
    }

    private fun getArtworkFromAlbum(albumId: String) {
        val albumService = AlbumAPI.service

        val result = object : Callback<HashMap<String, String>> {
            override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                Log.d("AlbumService", "Error$t")
            }

            override fun onResponse(call: Call<HashMap<String, String>>?, response: Response<HashMap<String, String>>?) {
                if (response != null) {
                    Log.d("AlbumService", "Artwork: ${response.body()}")
                    val albumArtwork = response.body()!!["artwork"]

                    if (albumArtwork != null) {
                        doAsync {
                            val url = getString(R.string.image_url, albumArtwork)
                            val artwork: Bitmap? = getArtwork(url)
                            uiThread {
                                Blurry.with(applicationContext)
                                    .radius(5)
                                    .sampling(5)
                                    .color(Color.argb(90, 255, 255, 255))
                                    .from(artwork)
                                    .into(imageView_bg)
                            }
                        }
                    } else {
                        Blurry.with(applicationContext)
                            .radius(5)
                            .sampling(5)
                            .color(Color.argb(90, 255, 255, 255))
                            .from(BitmapFactory.decodeResource(resources, R.drawable.artwork_placeholder))
                            .into(imageView_bg)
                    }
                }
            }
        }
        albumService.getArtworkOfAlbum(albumId).enqueue(result)
    }

    private fun getArtwork(urlString: String): Bitmap? {
        var result: Bitmap? = null
        val url = URL(urlString)
        try {
            val connection = url.openConnection() as HttpURLConnection
            val inputStream: InputStream = connection.inputStream
            result = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        } catch (e: Exception) {
            Log.d("ArtworkDebug", "$e")
        }
        return result
    }
}
