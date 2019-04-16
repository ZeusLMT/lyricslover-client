package com.zeuslmt.lyricslover.fragments.fragment_songs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zeuslmt.lyricslover.R

class FragmentAlbums : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_albums, container, false)
    }

    companion object {
        fun newInstance(): FragmentAlbums = FragmentAlbums()
    }
}