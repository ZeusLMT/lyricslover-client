package com.zeuslmt.lyricslover.MainActivity.ArtistsTab

import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Artist

class ArtistListAdapter (private val appContext: Context, val clickListener: (Artist) -> Unit ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataset: Array<Artist> = emptyArray()

    //ViewHolder class
    class ListViewHolder (private val itemView: View,
                          val artistName: TextView = itemView.findViewById(R.id.textView_artist),
                          val albumsCount: TextView = itemView.findViewById(R.id.textView_albums),
                          val tracksCount: TextView = itemView.findViewById(R.id.textView_tracks),
                          val itemDivider: View = itemView.findViewById(R.id.item_divider)) : RecyclerView.ViewHolder(itemView)

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

        if (thisArtist.albums == null) {
            holder.albumsCount.text = appContext.getString(R.string.textView_numberOfAlbums, 0)
        } else {
            holder.albumsCount.text = appContext.getString(R.string.textView_numberOfAlbums, thisArtist.albums.size)
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

}