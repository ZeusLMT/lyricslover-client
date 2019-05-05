package com.zeuslmt.lyricslover.NewSongActivity.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import com.zeuslmt.lyricslover.APIs.ArtistAPI
import com.zeuslmt.lyricslover.models.Artist
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.app.Activity.RESULT_OK
import android.provider.MediaStore
import android.net.Uri
import android.view.View
import android.widget.*
import com.zeuslmt.lyricslover.R


class NewAlbumDialog : DialogFragment() {
    companion object {
        private const val REQUEST_IMAGE_SELECT = 1
    }
    private lateinit var listener: NoticeDialogListener
    private var artists: Array<Artist> = emptyArray()
    private var newArtworkPath: String? = ""
    private var artistSelected = false

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, bundle: Bundle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.dialog_new_album, null))
                .setTitle(R.string.dialog_title_newAlbum)
                // Add action buttons
                .setPositiveButton(
                    R.string.button_label_save, null
                )
                .setNegativeButton(
                    R.string.button_label_cancel
                ) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        val d = (dialog as AlertDialog)

        if (!artistSelected) {
            getArtist {
                setupArtistSpinner(null)
            }
            artistSelected = true
        }

        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val albumTitleTIL = dialog.findViewById<TextInputLayout>(R.id.textInputLayout_albumName)
            val yearTIL = dialog.findViewById<TextInputLayout>(R.id.textInputLayout_year)
            val artistSpinner = dialog.findViewById<Spinner>(R.id.spinner_artist)

            if (checkSaveButtonState(artistSpinner, albumTitleTIL)) {
                val bundle = Bundle()
                bundle.putString("title", albumTitleTIL.editText!!.text.toString())
                bundle.putString("artist", artists[artistSpinner.selectedItemPosition]._id)

                if (yearTIL.editText!!.text != null) {
                    bundle.putString("year", yearTIL.editText!!.text.toString())
                }

                if (!newArtworkPath.isNullOrEmpty()) {
                    bundle.putString("artwork", newArtworkPath)
                }

                listener.onDialogPositiveClick(this, bundle)
                dismiss()
            }
        }

        dialog.findViewById<ImageView>(R.id.imageView_newArtwork).setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_SELECT)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_SELECT) {
                // Get the url from data
                val selectedImageUri = data!!.data
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    newArtworkPath = getPathFromURI(selectedImageUri)
                    Log.d("SelectImage", "Image Path : $newArtworkPath")
                    // Set the image in ImageView
                    dialog.findViewById<ImageView>(R.id.imageView_newArtwork).setImageURI(selectedImageUri)
                }
            }
        }
    }



    private fun checkSaveButtonState(artistSpinner: Spinner, titleTIL: TextInputLayout): Boolean {
        var isTitleNotEmpty: Boolean
        val isSpinnerSelected = artistSpinner.selectedItem != null

        if (titleTIL.editText!!.text.isBlank()) {
            titleTIL.error = getString(R.string.error_empty_albumName)
            isTitleNotEmpty = false
        } else {
            isTitleNotEmpty = true
        }

        return (isSpinnerSelected && isTitleNotEmpty)
    }

    private fun getArtist(onComplete: () -> Unit) {
        val artistService = ArtistAPI.service

        val result = object : Callback<Array<Artist>> {
            override fun onFailure(call: Call<Array<Artist>>, t: Throwable) {
                Log.d("AlbumService", "Error: $t")
            }

            override fun onResponse(call: Call<Array<Artist>>?, response: Response<Array<Artist>>?) {
                if (response != null) {
                    artists = response.body()!!
                    onComplete()
                }
            }
        }
        artistService.getAllArtists().enqueue(result)
    }

    private fun setupArtistSpinner(setSelectionManually: (() -> Unit)?) {
        val artistNames = ArrayList<String>()

        if (artists.isNotEmpty()) {
            for (artist in artists) {
                artistNames.add(artist.name)
            }
        } else {
            handleEmptyArtist()
        }

        val adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, artistNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        val artistSpinner = (dialog as AlertDialog).findViewById<Spinner>(R.id.spinner_artist)
        artistSpinner!!.adapter = adapter

        if (setSelectionManually != null) {
            setSelectionManually()
        }
    }

    private fun handleEmptyArtist() {
        Toast.makeText(activity, "Found no artist, consider adding one first!", Toast.LENGTH_LONG).show()
    }

    /* Get the real path from the URI */
    private fun getPathFromURI(contentUri: Uri): String? {
        var path = ""
        var imageId = ""

        var cursor = context!!.contentResolver.query(contentUri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            imageId = cursor.getString(0)
            imageId = imageId.substring(imageId.lastIndexOf(":") + 1)
            cursor.close()
        }

        cursor = context!!.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(imageId),
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            cursor.close()
        }
        return path
    }
}