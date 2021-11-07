package com.scheme

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController

class SetupDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.CompleteSetupTitle))
            .setMessage(getString(R.string.CompleteSetupMessage))
            .setPositiveButton(getString(R.string.CompleteSetupAction)) { _, _ ->
                if (findNavController().currentDestination?.id == R.id.view_pager_fragment) {
                    val direction = MainFragmentDirections.actionHomeToSettings()
                    findNavController().navigate(direction)
                }
            }

        val dialog: Dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        return dialog
    }
}