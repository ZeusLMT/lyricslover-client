package com.zeuslmt.lyricslover.MainActivity.SongsTab

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Song

class SongListAdapter (private val appContext: Context, val clickListener: (Song) -> Unit, val longClickListener: (Song) -> Boolean ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataset: Array<Song> = emptyArray()

    //ViewHolder class
    class ListViewHolder (private val itemView: View,
                          val songTitle: TextView = itemView.findViewById(R.id.textView_songTitle),
                          val artistName: TextView = itemView.findViewById(R.id.textView_artist),
                          val albumName: TextView = itemView.findViewById(R.id.textView_album),
                          val horizontalDivider: View = itemView.findViewById(R.id.album_artist_divider),
                          val itemDivider: View = itemView.findViewById(R.id.item_divider)) : RecyclerView.ViewHolder(itemView)

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
        } else {
            holder.horizontalDivider.visibility = View.GONE
            holder.albumName.visibility = View.GONE
        }

        if (position == dataset.lastIndex) {
            holder.itemDivider.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { clickListener(thisSong) }

        holder.itemView.setOnLongClickListener { longClickListener(thisSong) }
    }

    fun setData(newData: Array<Song>) {
        dataset = newData
        notifyDataSetChanged()
    }

}