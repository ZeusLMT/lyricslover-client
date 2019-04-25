package com.zeuslmt.lyricslover.NewSongActivity.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.zeuslmt.lyricslover.APIs.ArtistAPI
import com.zeuslmt.lyricslover.R
import com.zeuslmt.lyricslover.models.Artist
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewAlbumDialog : DialogFragment() {
    companion object {
        private const val REQUEST_IMAGE_SELECT = 1
    }
    private lateinit var listener: NoticeDialogListener
    private var artists: Array<Artist> = emptyArray()

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

        getArtist {
            setupArtistSpinner(null)
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
                listener.onDialogPositiveClick(this, bundle)
                dismiss()
            }
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

    private fun checkSaveButtonState(artistSpinner: Spinner, titleTIL: TextInputLayout): Boolean {
        var isTitleNotEmpty = false
        var isSpinnerSelected = false

        if (titleTIL.editText!!.text.isBlank()) {
            titleTIL.error = getString(R.string.error_empty_albumName)
            isTitleNotEmpty = false
        } else {
            isTitleNotEmpty = true
        }

        isSpinnerSelected = artistSpinner.selectedItem != null

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
        Toast.makeText(activity, "Test toast", Toast.LENGTH_SHORT).show()

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
}