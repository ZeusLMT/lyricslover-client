package com.zeuslmt.lyricslover.NewSongActivity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_new_song.*
import com.zeuslmt.lyricslover.R


class NewSongActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_IMAGE_SELECT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_song)

        supportActionBar?.title = "New Song"

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
