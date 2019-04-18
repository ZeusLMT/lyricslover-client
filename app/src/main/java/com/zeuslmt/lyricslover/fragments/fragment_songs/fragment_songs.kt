package com.zeuslmt.lyricslover.fragments.fragment_songs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zeuslmt.lyricslover.APIs.SongAPI
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Song
import kotlinx.android.synthetic.main.fragment_songs.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentSongs : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_songs, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button_getSongs.setOnClickListener {
            getSongsList()
        }
    }

    companion object {
        fun newInstance(): FragmentSongs = FragmentSongs()
    }

    private fun getSongsList() {
        val songService = SongAPI.service

        val result = object : Callback<Array<Song>> {
            override fun onFailure(call: Call<Array<Song>>, t: Throwable) {
                Log.d("SongService", "Error$t")
            }

            override fun onResponse(call: Call<Array<Song>>?, response: Response<Array<Song>>?) {
                if (response != null) {
                    val res: Array<Song> = response.body()!!
                    res.forEach { song ->
                        Log.d("SongService", song.toString())
                    }
                }
            }
        }
        songService.getAllSongs().enqueue(result)
    }
}