package com.zeuslmt.lyricslover.MainActivity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import com.zeuslmt.lyricslover.ItemDetails.ItemDetailsActivity
import com.zeuslmt.lyricslover.MainActivity.SongsTab.SongsFragment
import com.zeuslmt.lyricslover.NewSongActivity.NewSongActivity
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.SongDetailsActivity.SongDetailsActivity
import com.zeuslmt.lyricslover.fragments.fragment_songs.AlbumsFragment
import com.zeuslmt.lyricslover.fragments.fragment_songs.FragmentArtists
import com.zeuslmt.lyricslover.models.Album
import com.zeuslmt.lyricslover.models.Artist
import com.zeuslmt.lyricslover.models.Song
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SongsFragment.SongsFragListener, AlbumsFragment.AlbumsFragListener, FragmentArtists.ArtistsFragListener {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        setAppBarHeight()
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        button_add.setOnClickListener {
            val intent = Intent(this, NewSongActivity::class.java).apply {}
            startActivity(intent)
        }

    }

    private fun setAppBarHeight() {
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
        appBarLayout.layoutParams = CoordinatorLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            2*getStatusBarHeight() + dpToPx(48 + 56)
        )
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        Log.d("abc", "Statusbar: $result")
        return result
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources
            .displayMetrics
            .density
        return Math.round(dp.toFloat() * density)
    }

    //Listeners from fragments
    override fun onSongClick(item: Song) {
        Log.d("SongListener", item.title)
        val intent = Intent(this, SongDetailsActivity::class.java).apply {}
        intent.putExtra("_id", item._id)
        startActivity(intent)
    }

    override fun onAlbumClick(item: Album) {
        Log.d("AlbumListener", item.title)
        val intent = Intent(this, ItemDetailsActivity::class.java).apply {}
        intent.putExtra("itemType", "album")
        intent.putExtra("_id", item._id)
        intent.putExtra("title", item.title)
        startActivity(intent)
    }

    override fun onArtistClick(item: Artist) {
        Log.d("ArtistListener", item.name)
        val intent = Intent(this, ItemDetailsActivity::class.java).apply {}
        intent.putExtra("itemType", "artist")
        intent.putExtra("_id", item._id)
        intent.putExtra("title", item.name)
        startActivity(intent)
    }
}
