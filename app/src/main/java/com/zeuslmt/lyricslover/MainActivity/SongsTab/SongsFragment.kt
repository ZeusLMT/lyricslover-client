package com.zeuslmt.lyricslover.MainActivity.SongsTab

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
import com.zeuslmt.lyricslover.APIs.SongAPI
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Song
import kotlinx.android.synthetic.main.fragment_songs.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SongsFragment : Fragment() {
    companion object {
        fun newInstance(): SongsFragment =
            SongsFragment()
    }

    private var songs: Array<Song> = emptyArray()
    private lateinit var adapter: SongListAdapter
    private var activityCallBack: SongsFragListener ?= null

    interface SongsFragListener{
        fun onSongClick(item: Song)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_songs, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = SongListAdapter(context!!,{item: Song -> onSongClick(item)}, {item: Song -> onSongLongClick(item)})
        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        getSongsList()
    }

    private fun getSongsList() {
        progressBar_song.visibility = View.VISIBLE
        val songService = SongAPI.service

        val result = object : Callback<Array<Song>> {
            override fun onFailure(call: Call<Array<Song>>, t: Throwable) {
                Log.d("SongService", "Error: $t")
            }

            override fun onResponse(call: Call<Array<Song>>?, response: Response<Array<Song>>?) {
                if (response != null) {
                    songs = response.body()!!
                    adapter.setData(songs)
                    progressBar_song.visibility = View.GONE
                }
            }
        }
        songService.getAllSongs().enqueue(result)
    }

    private fun deleteSong(songId: String) {
        val songService = SongAPI.service

        val result = object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("SongService", "Error: $t")
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response != null) {
                    val statusCode = response.code()
                    Log.d("SongService", statusCode.toString())
                    if (statusCode in 200..201) {
                        Toast.makeText(context, "Song deleted!", Toast.LENGTH_SHORT).show()

                        //reload the list
                        getSongsList()
                    }
                }
            }
        }
        songService.deleteSongById(songId).enqueue(result)
    }

    private fun setUpRecyclerView() {
        recyclerView_songList.layoutManager = LinearLayoutManager(context)
        recyclerView_songList.adapter = adapter
    }

    private fun onSongClick(song: Song) {
        //Pass post to MainActivity
        activityCallBack!!.onSongClick(song)
    }

    private fun onSongLongClick(song: Song): Boolean {
        val deleteDialog = AlertDialog.Builder(context!!)
            .setTitle("Delete song?")
            .setMessage("Do you want to delete this song?")
            .setNegativeButton(R.string.button_label_cancel) { dialog, _ ->
                dialog.cancel()
            }.setPositiveButton(R.string.button_label_ok) {_, _ ->
                deleteSong(song._id)
            }
            .create()
        deleteDialog.show()
        return true
    }

    override fun onAttach(context: Context){
        super.onAttach(context)
        activityCallBack = context as SongsFragListener
    }
}