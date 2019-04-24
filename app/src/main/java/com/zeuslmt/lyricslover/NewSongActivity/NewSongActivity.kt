package com.zeuslmt.lyricslover.NewSongActivity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.zeuslmt.lyricslover.APIs.AlbumAPI
import com.zeuslmt.lyricslover.APIs.ArtistAPI
import com.zeuslmt.lyricslover.R
import kotlinx.android.synthetic.main.activity_new_song.*
import com.zeuslmt.lyricslover.models.Album
import com.zeuslmt.lyricslover.models.Artist
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL


class NewSongActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    companion object {
        private const val REQUEST_IMAGE_SELECT = 1
    }

    private var albums: Array<Album> = emptyArray()
    private var artists: Array<Artist> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_song)
        supportActionBar?.title = "New Song"

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED) {
//            Log.d("abc", "not granted")
//            ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
//        }
        getAlbums { setupAlbumSpinner() }
        getArtist { setupArtistSpinner() }

        button_cancel.setOnClickListener {
            finish()
        }

        button_save.setOnClickListener {

        }
    }

    private fun getAlbums(onComplete: () -> Unit) {
//        progressBar_song.visibility = View.VISIBLE
        val albumService = AlbumAPI.service

        val result = object : Callback<Array<Album>> {
            override fun onFailure(call: Call<Array<Album>>, t: Throwable) {
                Log.d("AlbumService", "Error$t")
            }

            override fun onResponse(call: Call<Array<Album>>?, response: Response<Array<Album>>?) {
                if (response != null) {
                    albums = response.body()!!
                    onComplete()
                }
            }
        }
        albumService.getAllAlbums().enqueue(result)
    }

    private fun setupAlbumSpinner() {
        val albumNames = ArrayList<String>()

        for (album in albums) {
            albumNames.add(album.title)
        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, albumNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner_album.adapter = adapter
        spinner_album.onItemSelectedListener = this
    }

    private fun getArtist(onComplete: () -> Unit) {
//        progressBar_song.visibility = View.VISIBLE
        val artistService = ArtistAPI.service

        val result = object : Callback<Array<Artist>> {
            override fun onFailure(call: Call<Array<Artist>>, t: Throwable) {
                Log.d("AlbumService", "Error$t")
            }

            override fun onResponse(call: Call<Array<Artist>>?, response: Response<Array<Artist>>?) {
                if (response != null) {
                    artists = response.body()!!
                    onComplete()
                }
            }
        }
        artistService.getAllArtists().enqueue(result)
    }

    private fun setupArtistSpinner() {
        val artistNames = ArrayList<String>()

        for (artist in artists) {
            artistNames.add(artist.name)
        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, artistNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner_artist.adapter = adapter
        spinner_artist.onItemSelectedListener = this
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
            Log.d("artworkDebug", "$e")
        }
        return result
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        if(parent!!.id == R.id.spinner_album)
        {
            Log.d("abc", "Nothing selected - album")
        }
        else if(parent.id == R.id.spinner_artist)
        {
            Log.d("abc", "Nothing selected - artist")
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(parent!!.id == R.id.spinner_album)
        {
            if (albums[position].artwork != null) {
                doAsync {
                    val url = getString(R.string.image_url, albums[position].artwork)
                    val artwork: Bitmap? = getArtwork(url)
                    uiThread {
                        imageView_artwork.setImageBitmap(artwork)
//                    progressBar.visibility = View.GONE
                    }
                }
            } else {
                imageView_artwork.setImageDrawable(getDrawable(R.drawable.artwork_placeholder))
            }
        }
        else if(parent.id == R.id.spinner_artist)
        {
            //do this
        }
    }
}
