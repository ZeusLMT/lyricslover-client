package com.zeuslmt.lyricslover.fragments.fragment_songs

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.zeuslmt.lyricslover.APIs.ArtistAPI
import com.zeuslmt.lyricslover.MainActivity.ArtistsTab.ArtistListAdapter
import com.zeuslmt.lyricslover.NewSongActivity.dialogs.NewArtistDialog
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Artist
import kotlinx.android.synthetic.main.fragment_artists.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentArtists : Fragment() {
    companion object {
        fun newInstance(): FragmentArtists = FragmentArtists()
        private const val EDIT_ARTIST_DIALOG_TAG = "EditArtistDialog"
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

        adapter = ArtistListAdapter(context!!) { item: Artist -> onArtistClick(item)}
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

    override fun onAttach(context: Context){
        super.onAttach(context)
        activityCallBack = context as ArtistsFragListener
    }
}