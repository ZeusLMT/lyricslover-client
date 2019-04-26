package com.zeuslmt.lyricslover.fragments.fragment_songs

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.zeuslmt.lyricslover.APIs.ArtistAPI
import com.zeuslmt.lyricslover.MainActivity.ArtistsTab.ArtistListAdapter
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Artist
import kotlinx.android.synthetic.main.fragment_artists.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentArtists : Fragment() {
    companion object {
        fun newInstance(): FragmentArtists = FragmentArtists()
    }

    private var artists: Array<Artist> = emptyArray()
    private lateinit var adapter: ArtistListAdapter
    private var activityCallBack: ArtistsFragListener ?= null

    interface ArtistsFragListener{
        fun onArtistClick(item: Artist)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artists, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = ArtistListAdapter(context!!, {item: Artist -> onArtistClick(item)}, {item: Artist -> onArtistLongClick(item)})
        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        getArtistsList()
    }

    private fun getArtistsList() {
        progressBar_artist.visibility = View.VISIBLE
        val artistService = ArtistAPI.service

        val result = object : Callback<Array<Artist>> {
            override fun onFailure(call: Call<Array<Artist>>, t: Throwable) {
                Log.d("SongService", "Error$t")
            }

            override fun onResponse(call: Call<Array<Artist>>?, response: Response<Array<Artist>>?) {
                if (response != null) {
                    artists = response.body()!!
                    adapter.setData(artists)
                    progressBar_artist.visibility = View.GONE
                }
            }
        }
        artistService.getAllArtists().enqueue(result)
    }

    private fun setUpRecyclerView() {
        recyclerView_artistList.layoutManager = LinearLayoutManager(context)
        recyclerView_artistList.adapter = adapter
    }

    private fun onArtistClick(artist: Artist) {
        //Pass post to MainActivity
        activityCallBack!!.onArtistClick(artist)
    }

    private fun onArtistLongClick(artist: Artist): Boolean {
        val deleteDialog = AlertDialog.Builder(context!!)
            .setTitle("Delete artist?")
            .setMessage("Do you want to delete this artist?")
            .setNegativeButton(R.string.button_label_cancel) { dialog, _ ->
                dialog.cancel()
            }.setPositiveButton(R.string.button_label_ok) {_, _ ->
                deleteArtist(artist._id)
            }
            .create()
        deleteDialog.show()

        return true
    }

    private fun deleteArtist(artistId: String) {
//        val artistService = ArtistAPI.service
//
//        val result = object : Callback<ResponseBody> {
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.d("ArtistService", "Error: $t")
//            }
//
//            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
//                if (response != null) {
//                    val statusCode = response.code()
//                    Log.d("ArtistService", statusCode.toString())
//                    if (statusCode in 200..201) {
//                        Toast.makeText(context, "Song deleted!", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//        artistService.deleteArtistById(artistId).enqueue(result)

        Toast.makeText(context, "Artist deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context){
        super.onAttach(context)
        activityCallBack = context as ArtistsFragListener
    }
}