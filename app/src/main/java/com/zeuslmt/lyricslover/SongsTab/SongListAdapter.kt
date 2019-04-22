package com.zeuslmt.lyricslover.SongsTab

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Song

class SongListAdapter (private val appContext: Context, val clickListener: (Song) -> Unit ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataset: Array<Song> = emptyArray()

    //ViewHolder class
    class ListViewHolder (private val itemView: View,
                          val songTitle: TextView = itemView.findViewById(R.id.textView_songTitle),
                          val artistName: TextView = itemView.findViewById(R.id.textView_artist),
                          val albumName: TextView = itemView.findViewById(R.id.textView_album)) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_list_item, parent, false)
        return ListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val thisSong = dataset[position]

        (holder as ListViewHolder).songTitle.text = thisSong.title
        holder.artistName.text = thisSong.artist.name
        if (thisSong.album != null) {
            holder.albumName.text = thisSong.album.title
        }

        holder.itemView.setOnClickListener { clickListener(thisSong) }
    }

    fun setData(newData: Array<Song>) {
        dataset = newData
        notifyDataSetChanged()
    }

}