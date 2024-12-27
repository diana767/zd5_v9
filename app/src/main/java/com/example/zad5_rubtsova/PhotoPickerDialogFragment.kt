package com.example.zad5_rubtsova

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PhotoPickerDialogFragment : DialogFragment() {

    private var listener: OnPhotoSelectedListener? = null

    interface OnPhotoSelectedListener {
        fun onPhotoSelected(photo: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnPhotoSelectedListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val recyclerView = RecyclerView(requireContext())
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        val photos = listOf(
            R.drawable.photo1,
            R.drawable.photo2,
            R.drawable.photo3,
            R.drawable.photo4,
            R.drawable.photo5
        )

        val adapter = PhotoAdapter(photos) { photoResId ->
            val photoName = resources.getResourceEntryName(photoResId)
            listener?.onPhotoSelected(photoName)
            dismiss()
        }

        recyclerView.adapter = adapter

        builder.setView(recyclerView)
            .setTitle("Выберите фотографию")
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }

        return builder.create()
    }
}