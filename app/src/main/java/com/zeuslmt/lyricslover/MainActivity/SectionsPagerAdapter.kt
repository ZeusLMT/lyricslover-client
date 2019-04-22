package com.zeuslmt.lyricslover.MainActivity

import android.content.res.Resources
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.fragments.fragment_songs.AlbumsFragment
import com.zeuslmt.lyricslover.fragments.fragment_songs.FragmentArtists
import com.zeuslmt.lyricslover.SongsTab.SongsFragment

class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> SongsFragment.newInstance()
        1 -> FragmentArtists.newInstance()
        2 -> AlbumsFragment.newInstance()
        else -> null
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> Resources.getSystem().getString(R.string.tab_text_1)
        1 -> Resources.getSystem().getString(R.string.tab_text_2)
        2 -> Resources.getSystem().getString(R.string.tab_text_3)
        else -> ""
    }

    override fun getCount(): Int = 3
}