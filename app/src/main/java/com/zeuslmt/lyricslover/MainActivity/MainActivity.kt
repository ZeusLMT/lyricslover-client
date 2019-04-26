package com.zeuslmt.lyricslover.MainActivity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
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
    }

    override fun onArtistClick(item: Artist) {
        Log.d("ArtistListener", item.name)
    }
}
