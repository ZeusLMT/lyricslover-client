package com.zeuslmt.lyricslover.MainActivity.AlbumsTab

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Album
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class AlbumGridAdapter (private val appContext: Context, val clickListener: (Album) -> Unit ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataset: Array<Album> = emptyArray()

    //ViewHolder class
    class GridViewHolder (private val itemView: View,
                          val albumTitle: TextView = itemView.findViewById(R.id.textView_albumTitle),
                          val artwork: ImageView = itemView.findViewById(R.id.imageView_artwork),
                          val artistName: TextView = itemView.findViewById(R.id.textView_artist),
                          val horizontalDivider: View = itemView.findViewById(R.id.year_artist_divider),
                          val year: TextView = itemView.findViewById(R.id.textView_year),
                          val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar_artwork)) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.album_grid_item, parent, false)
        return GridViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as GridViewHolder).progressBar.visibility = View.VISIBLE

        val thisAlbum = dataset[position]

        holder.albumTitle.text = thisAlbum.title
        holder.artistName.text = thisAlbum.artist.name
        if (thisAlbum.year != null) {
            holder.year.text = thisAlbum.year.toString()
        } else {
            holder.horizontalDivider.visibility = View.GONE
            holder.year.visibility = View.GONE
        }

        if (thisAlbum.artwork != null) {
            doAsync {
                val url = appContext.getString(R.string.image_url, thisAlbum.artwork)
                val artwork: Bitmap? = getArtwork(url)
                uiThread {
                    holder.artwork.setImageBitmap(artwork)
                    holder.progressBar.visibility = View.GONE
                }
            }
        } else {
            holder.progressBar.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { clickListener(thisAlbum) }
    }

    fun setData(newData: Array<Album>) {
        dataset = newData
        notifyDataSetChanged()
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

}