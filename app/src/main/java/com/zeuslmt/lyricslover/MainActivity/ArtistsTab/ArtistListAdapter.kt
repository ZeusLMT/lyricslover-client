package com.zeuslmt.lyricslover.MainActivity.ArtistsTab

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zeuslmt.lyricslover.APIs.AlbumAPI
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Artist
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ArtistListAdapter (private val appContext: Context, val clickListener: (Artist) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataset: Array<Artist> = emptyArray()

    //ViewHolder class
    class ListViewHolder (private val itemView: View,
                          val artistName: TextView = itemView.findViewById(R.id.textView_artist),
                          val albumsCount: TextView = itemView.findViewById(R.id.textView_albums),
                          val tracksCount: TextView = itemView.findViewById(R.id.textView_tracks),
                          val itemDivider: View = itemView.findViewById(R.id.item_divider),
                          val artistAvatar: ImageView = itemView.findViewById(R.id.imageView_artist)) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.artist_list_item, parent, false)
        return ListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val thisArtist = dataset[position]

        (holder as ListViewHolder).artistName.text = thisArtist.name
        holder.albumsCount.text = appContext.getString(R.string.textView_numberOfAlbums, thisArtist.albums!!.size)

        if (thisArtist.albums.isNotEmpty()) {
            getArtworkFromAlbum(thisArtist.albums[0]._id) { artwork ->
                holder.artistAvatar.setImageBitmap(artwork)
            }
        } else {
            holder.artistAvatar.setImageDrawable(appContext.getDrawable(R.drawable.avatar_placeholder))
        }

        if (thisArtist.songs == null) {
            holder.tracksCount.text = appContext.getString(R.string.textView_numberOfTracks, 0)
        } else {
            holder.tracksCount.text = appContext.getString(R.string.textView_numberOfTracks, thisArtist.songs.size)
        }

        if (position == dataset.lastIndex) {
            holder.itemDivider.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { clickListener(thisArtist) }
    }

    fun setData(newData: Array<Artist>) {
        dataset = newData
        notifyDataSetChanged()
    }

    private fun getArtworkFromAlbum(albumId: String, onResult: (Bitmap?) -> Unit) {
        val albumService = AlbumAPI.service

        val result = object : Callback<HashMap<String, String>> {
            override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                Log.d("ArtistService", "Error$t")
            }

            override fun onResponse(call: Call<HashMap<String, String>>?, response: Response<HashMap<String, String>>?) {
                if (response != null) {
                    Log.d("ArtistService", "Artwork: ${response.body()}")
                    val artworkPath = response.body()!!["artwork"]

                    if (artworkPath != null) {
                        doAsync {
                            val url = appContext.getString(R.string.image_url, artworkPath)
                            val artwork: Bitmap? = getArtwork(url)
                            uiThread {
                                onResult(artwork)
                            }
                        }
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