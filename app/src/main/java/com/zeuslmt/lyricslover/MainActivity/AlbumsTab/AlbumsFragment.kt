package com.zeuslmt.lyricslover.fragments.fragment_songs

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.zeuslmt.lyricslover.APIs.AlbumAPI
import com.zeuslmt.lyricslover.MainActivity.AlbumsTab.AlbumGridAdapter
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Album
import kotlinx.android.synthetic.main.fragment_albums.*
import okhttp3.ResponseBody
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

        adapter = AlbumGridAdapter(context!!, { item: Album -> onAlbumClick(item) }, { item: Album -> onAlbumLongClick(item) })
        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        getAlbumsList()
    }

    private fun getAlbumsList() {
        progressBar_album.visibility = View.VISIBLE

        val albumService = AlbumAPI.service

        val result = object : Callback<Array<Album>> {
            override fun onFailure(call: Call<Array<Album>>, t: Throwable) {
                Log.d("AlbumService", "Error$t")
            }

            override fun onResponse(call: Call<Array<Album>>?, response: Response<Array<Album>>?) {
                if (response != null) {
                    albums = response.body()!!
                    adapter.setData(albums)
                    progressBar_album.visibility = View.GONE
                }
            }
        }

        if (arguments == null) {
            albumService.getAllAlbums().enqueue(result)
        } else {
            val artistId = arguments!!.getString("_id")
            albumService.getAlbumsByArtist(artistId!!).enqueue(result)
        }
    }


    private fun setUpRecyclerView() {
        recyclerView_albumGrid.layoutManager = GridLayoutManager(context, 2)
        recyclerView_albumGrid.adapter = adapter
    }


    private fun onAlbumLongClick(album: Album): Boolean {
        val deleteDialog = AlertDialog.Builder(context!!)
            .setTitle("Delete album?")
            .setMessage("Do you want to delete this album?")
            .setNegativeButton(R.string.button_label_cancel) { dialog, _ ->
                dialog.cancel()
            }.setPositiveButton(R.string.button_label_ok) {_, _ ->
                deleteAlbum(album._id)
            }
            .create()
        deleteDialog.show()
        return true
    }

    private fun deleteAlbum(albumId: String) {
        val albumService = AlbumAPI.service

        val result = object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("AlbumService", "Error: $t")
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response != null) {
                    val statusCode = response.code()
                    Log.d("AlbumService", statusCode.toString())
                    if (statusCode in 200..201) {
                        Toast.makeText(context, "Album deleted!", Toast.LENGTH_SHORT).show()

                        //reload the list
                        getAlbumsList()
                    }
                }
            }
        }
        albumService.deleteAlbumById(albumId).enqueue(result)

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