package com.zeuslmt.lyricslover.fragments.fragment_songs

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zeuslmt.lyricslover.APIs.AlbumAPI
import com.zeuslmt.lyricslover.AlbumsTab.AlbumGridAdapter
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Album
import kotlinx.android.synthetic.main.fragment_albums.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumsFragment : Fragment() {
    companion object {
        fun newInstance(): AlbumsFragment = AlbumsFragment()
    }

    private var albums: Array<Album> = emptyArray()
    private lateinit var adapter: AlbumGridAdapter
    private var activityCallBack: AlbumsFragListener? = null

    interface AlbumsFragListener {
        fun onAlbumClick(item: Album)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_albums, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = AlbumGridAdapter(context!!) { item: Album -> onAlbumClick(item) }
        setUpRecyclerView()
        getSongsList()
    }

    private fun getSongsList() {
        progressBar_album.visibility = View.VISIBLE

        val albumService = AlbumAPI.service

        val result = object : Callback<Array<Album>> {
            override fun onFailure(call: Call<Array<Album>>, t: Throwable) {
                Log.d("SongService", "Error$t")
            }

            override fun onResponse(call: Call<Array<Album>>?, response: Response<Array<Album>>?) {
                if (response != null) {
                    albums = response.body()!!
                    Log.d("abc", albums.size.toString())
                    adapter.setData(albums)
                    progressBar_album.visibility = View.GONE
                }
            }
        }
        albumService.getAllAlbums().enqueue(result)
    }


    private fun setUpRecyclerView() {
        recyclerView_albumGrid.layoutManager = GridLayoutManager(context, 2)
        recyclerView_albumGrid.adapter = adapter
    }

    private fun onAlbumClick(album: Album) {
        //Pass post to MainActivity
        activityCallBack!!.onAlbumClick(album)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallBack = context as AlbumsFragListener
    }

}