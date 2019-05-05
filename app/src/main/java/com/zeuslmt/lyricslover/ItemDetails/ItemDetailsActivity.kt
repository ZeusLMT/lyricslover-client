package com.zeuslmt.lyricslover.ItemDetails

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zeuslmt.lyricslover.MainActivity.SongsTab.SongsFragment
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.SongDetailsActivity.SongDetailsActivity
import com.zeuslmt.lyricslover.fragments.fragment_songs.AlbumsFragment
import com.zeuslmt.lyricslover.models.Album
import com.zeuslmt.lyricslover.models.Song


class ItemDetailsActivity : AppCompatActivity(), SongsFragment.SongsFragListener, AlbumsFragment.AlbumsFragListener {
    private lateinit var type: String
    private lateinit var itemId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)

        supportActionBar?.title = intent.getStringExtra("title")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setBackgroundDrawable(getDrawable(R.drawable.ic_action_bar))

        type = intent.getStringExtra("itemType")
        itemId = intent.getStringExtra("_id")

        when(type) {
            "album" -> {
                val bundle = Bundle()
                bundle.putString("_id", itemId)
                val songsFragment = SongsFragment.newInstance()
                songsFragment.arguments = bundle
                supportFragmentManager.beginTransaction().add(R.id.frameLayout_content, songsFragment).commit()

            }

            "artist" -> {
                val bundle = Bundle()
                bundle.putString("_id", itemId)
                val albumsFragment = AlbumsFragment.newInstance()
                albumsFragment.arguments = bundle
                supportFragmentManager.beginTransaction().add(R.id.frameLayout_content, albumsFragment).commit()

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSongClick(item: Song) {
        val intent = Intent(this, SongDetailsActivity::class.java).apply {}
        intent.putExtra("_id", item._id)
        startActivity(intent)
    }

    override fun onAlbumClick(item: Album) {
        val intent = Intent(this, ItemDetailsActivity::class.java).apply {}
        intent.putExtra("itemType", "album")
        intent.putExtra("_id", item._id)
        intent.putExtra("title", item.title)
        startActivity(intent)
    }
}
