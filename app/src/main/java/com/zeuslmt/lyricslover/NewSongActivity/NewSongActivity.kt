package com.zeuslmt.lyricslover.NewSongActivity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.zeuslmt.lyricslover.APIs.AlbumAPI
import com.zeuslmt.lyricslover.APIs.ArtistAPI
import com.zeuslmt.lyricslover.APIs.SongAPI
import com.zeuslmt.lyricslover.NewSongActivity.dialogs.NewArtistDialog
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Album
import com.zeuslmt.lyricslover.models.Artist
import com.zeuslmt.lyricslover.models.NewArtist
import com.zeuslmt.lyricslover.models.NewSong
import kotlinx.android.synthetic.main.activity_new_song.*
import kotlinx.android.synthetic.main.dialog_new_artist.*
import org.jetbrains.anko.AlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL




class NewSongActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, TextWatcher, NewArtistDialog.NoticeDialogListener {
    companion object {
        private const val REQUEST_IMAGE_SELECT = 1
        private const val NEW_ARTIST_DIALOG_TAG = "NewArtistDialog"
        private const val NEW_ALBUM_DIALOG_TAG = "NewAlbumDialog"
    }

    private var albums: Array<Album> = emptyArray()
    private var artists: Array<Artist> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_song)
        supportActionBar?.title = "New Song"

        checkSaveButtonState()

        spinner_album.onItemSelectedListener = this
        spinner_artist.onItemSelectedListener = this

        textInputLayout_title.editText!!.addTextChangedListener(this)
        textInputLayout_lyrics.editText!!.addTextChangedListener(this)

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED) {
//            Log.d("abc", "not granted")
//            ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
//        }

        getArtist { setupArtistSpinner(null) }

        button_cancel.setOnClickListener {
            finish()
        }

        button_save.setOnClickListener {
            onSaveButton()
        }

        button_newArtist.setOnClickListener {
            val newArtistDialog = NewArtistDialog()
            newArtistDialog.show(supportFragmentManager, NEW_ARTIST_DIALOG_TAG)
        }
    }

    //Overrides for Text Watcher

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkSaveButtonState()
    }


    //Overrides for Spinner Item Select Listener

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d("abc", "Nothing selected")
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
            Log.d("abc", "onItemSelected - album")
            setSelectedAlbumArtwork(albums[position].artwork)
        }
        else if(parent.id == R.id.spinner_artist)
        {
            Log.d("abc", "onItemSelected - artist")
            val artistId = artists[position]._id
            getAlbumsByArtist(artistId) {
                setupAlbumSpinner()
            }
        }
        checkSaveButtonState()
    }

    //Override for Dialogs Listener

    override fun onDialogPositiveClick(dialog: DialogFragment, bundle: Bundle) {
        when (dialog.tag) {
            NEW_ALBUM_DIALOG_TAG -> {

            }

            NEW_ARTIST_DIALOG_TAG -> {
                val artistName = bundle.getString("name")
                Log.d("abc", "New artist name: $artistName")
                createNewArtist(artistName!!)
            }
        }
    }


    //Private methods

    private fun checkSaveButtonState() {
        Log.d("abc", "check save button")
        val emptyTitle = textInputLayout_title.editText!!.text.isBlank()
        val emptyLyrics = textInputLayout_lyrics.editText!!.text.isBlank()
        val artistNotSelected = spinner_artist.selectedItem == null
        val albumNotSelected = spinner_album.selectedItem == null

        Log.d("abc", "$emptyTitle - $emptyLyrics - $artistNotSelected - $albumNotSelected")

        button_save.isEnabled = !(emptyTitle || emptyLyrics || artistNotSelected || albumNotSelected)
        Log.d("abc", button_save.isEnabled.toString())

        button_save.alpha = if (button_save.isEnabled) 1.toFloat() else 0.3.toFloat()
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

        for (album in albums) {
            albumNames.add(album.title)
        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, albumNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinner_album.adapter = adapter

        if(albums.isEmpty()) {
            handleEmptyAlbum()
        }
    }

    private fun handleEmptyAlbum() {
        imageView_artwork.setImageDrawable(getDrawable(R.drawable.artwork_placeholder))
        Toast.makeText(this, "No album found for this artist, consider adding one!", Toast.LENGTH_LONG).show()

        checkSaveButtonState()
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

    private fun setupArtistSpinner(setSelectionManually: (() -> Unit)?) {
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

        if (setSelectionManually != null) {
            setSelectionManually()
        }
    }

    private fun handleEmptyArtist() {
        Toast.makeText(this, "Found no album and artist, consider adding one!", Toast.LENGTH_LONG).show()

        checkSaveButtonState()
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
        finish()
    }

    private fun createNewArtist(artistName: String) {
        val artistService = ArtistAPI.service

        val result = object : Callback<NewArtist> {
            override fun onFailure(call: Call<NewArtist>, t: Throwable) {
                Log.d("SongService", "Error: $t")
            }

            override fun onResponse(call: Call<NewArtist>?, response: Response<NewArtist>?) {
                if (response != null) {
                    Log.d("SongService", response.body().toString())
                    val newArtistId = response.body()!!._id

                    //refresh artist list and auto-select the newly created artist
                    getArtist {
                        setupArtistSpinner {
                            for ((index, artist) in artists.withIndex()) {
                                if (artist._id == newArtistId) {
                                    spinner_artist.setSelection(index)
                                }
                            }
                    }
                    }
                }
            }
        }
        artistService.addNewArtist(artistName).enqueue(result)
    }
}
