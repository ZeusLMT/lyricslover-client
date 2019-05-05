package com.zeuslmt.lyricslover.NewSongActivity.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.ImageView
import com.zeuslmt.lyricslover.R

class NewArtistDialog : DialogFragment() {
    private lateinit var listener: NoticeDialogListener
    private var isEdit = false
    private var currentName = ""

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, bundle: Bundle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.dialog_new_artist, null))
                .setTitle(R.string.dialog_title_newArtist)
                // Add action buttons
                .setPositiveButton(R.string.button_label_save, null
                )
                .setNegativeButton(R.string.button_label_cancel
                ) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        val d = (dialog as AlertDialog)

        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val artistTIL = dialog.findViewById<TextInputLayout>(R.id.textInputLayout_artistName)
            val artistName = artistTIL.editText!!.text.toString()
            when {
                artistName.isBlank() -> {
                    artistTIL.error = getString(R.string.error_empty_artistName)
                }

                artistName == currentName -> dismiss()

                else -> {
                    val bundle = Bundle()
                    bundle.putString("name", artistName)
                    listener.onDialogPositiveClick(this, bundle)
                    dismiss()
                }
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
}