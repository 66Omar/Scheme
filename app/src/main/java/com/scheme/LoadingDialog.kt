package com.scheme

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment

class LoadingDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.loading_dialog, null)

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.isIndeterminate = true

        val builder = AlertDialog.Builder(requireContext())
            .setView(view)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setDimAmount(0.1f)

        return dialog
    }
}