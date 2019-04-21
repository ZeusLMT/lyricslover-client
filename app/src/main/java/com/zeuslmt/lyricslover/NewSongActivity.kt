package com.zeuslmt.lyricslover

import android.Manifest
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_new_song.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log


class NewSongActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_IMAGE_SELECT = 1
    }
    var imgPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_song)

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED) {
//            Log.d("abc", "not granted")
//            ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
//        }

        button_cancel.setOnClickListener {
            finish()
        }
    }
}
