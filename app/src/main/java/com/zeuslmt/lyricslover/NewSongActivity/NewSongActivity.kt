package com.zeuslmt.lyricslover.NewSongActivity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.zeuslmt.lyricslover.APIs.AlbumAPI
import com.zeuslmt.lyricslover.APIs.ArtistAPI
import com.zeuslmt.lyricslover.APIs.SongAPI
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Album
import com.zeuslmt.lyricslover.models.Artist
import com.zeuslmt.lyricslover.models.NewSong
import kotlinx.android.synthetic.main.activity_new_song.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
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

        spinner_album.onItemSelectedListener = this
        spinner_artist.onItemSelectedListener = this

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED) {
//            Log.d("abc", "not granted")
//            ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
//        }

        getArtist { setupArtistSpinner() }

        button_cancel.setOnClickListener {
            finish()
        }

        button_save.setOnClickListener {
            onSaveButton()
        }
    }

    private fun getAlbumsByArtist(artistId: String, onComplete: () -> Unit) {
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
        albumService.getAlbumsByArtist(artistId).enqueue(result)
    }

    private fun setupAlbumSpinner() {
        val albumNames = ArrayList<String>()

        if(albums.isNotEmpty()) {
            for (album in albums) {
                albumNames.add(album.title)
            }
        } else {
            handleEmptyAlbum()
        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, albumNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner_album.adapter = adapter
    }

    private fun handleEmptyAlbum() {
        imageView_artwork.setImageDrawable(getDrawable(R.drawable.artwork_placeholder))
        Toast.makeText(this, "No album found for this artist, consider adding one!", Toast.LENGTH_LONG).show()

        //checkSaveButtonState()
    }

    private fun getArtist(onComplete: () -> Unit) {
//        progressBar_song.visibility = View.VISIBLE
        val artistService = ArtistAPI.service

        val result = object : Callback<Array<Artist>> {
            override fun onFailure(call: Call<Array<Artist>>, t: Throwable) {
                Log.d("AlbumService", "Error: $t")
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

        if (artists.isNotEmpty()) {
            for (artist in artists) {
                artistNames.add(artist.name)
            }
        } else {
            handleEmptyArtist()
        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, artistNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner_artist.adapter = adapter
    }

    private fun handleEmptyArtist() {
        Toast.makeText(this, "Found no album and artist, consider adding one!", Toast.LENGTH_LONG).show()

        //checkSaveButtonState()
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

    private fun setSelectedAlbumArtwork(artwork: String?) {
        if (artwork != null) {
            doAsync {
                val url = getString(R.string.image_url, artwork)
                val artworkBitmap: Bitmap? = getArtwork(url)
                uiThread {
                    imageView_artwork.setImageBitmap(artworkBitmap)
//                    progressBar.visibility = View.GONE
                }
            }
        } else {
            imageView_artwork.setImageDrawable(getDrawable(R.drawable.artwork_placeholder))
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d("abc", "Nothing selected")
        if(parent!!.id == R.id.spinner_album)
        {
            Log.d("abc", "Nothing selected - album")
            setSelectedAlbumArtwork(null)
        }
        else if(parent.id == R.id.spinner_artist)
        {
            Log.d("abc", "Nothing selected - artist")
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(parent!!.id == R.id.spinner_album)
        {
            Log.d("abc", "onItemSelected - album")
            setSelectedAlbumArtwork(albums[position].artwork)
        }
        else if(parent.id == R.id.spinner_artist)
        {
            val artistId = artists[position]._id
            getAlbumsByArtist(artistId) {
                setupAlbumSpinner()
            }
        }
    }

    private fun onSaveButton() {
        val newSongTitle = textInputLayout_title.editText!!.text.toString()
        val artistId = artists[spinner_artist.selectedItemPosition]._id
        val albumId = albums[spinner_album.selectedItemPosition]._id
        val lyrics = textInputLayout_lyrics.editText!!.text.toString()

        val songService = SongAPI.service

        val result = object : Callback<NewSong> {
            override fun onFailure(call: Call<NewSong>, t: Throwable) {
                Log.d("SongService", "Error: $t")
            }

            override fun onResponse(call: Call<NewSong>?, response: Response<NewSong>?) {
                if (response != null) {
                    Log.d("SongService", response.body().toString())
                }
            }
        }
        songService.addNewSong(newSongTitle, artistId, albumId, lyrics).enqueue(result)
    }
}
