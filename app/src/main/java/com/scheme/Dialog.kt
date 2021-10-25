package com.scheme

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager.LayoutParams
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.scheme.utilities.AlarmUtils
import com.scheme.utilities.NotificationSender
import com.scheme.viewModels.DialogViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class Dialog : DialogFragment() {
    private lateinit var spinner: Spinner
    private lateinit var editText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val viewModel = ViewModelProvider(this).get(DialogViewModel::class.java)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(viewModel.getLayout(), null)



        val builder = AlertDialog.Builder(requireContext())
            .setTitle(viewModel.getTitle())
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save) { _, _ ->
                when (viewModel.card) {
                    0 -> viewModel.onSave(editText.text.toString())
                    5 -> viewModel.onSave(editText.text.toString())
                    else -> {
                        if (spinner.selectedItem != null) {
                            viewModel.onSave(spinner.selectedItem.toString())
                            cancelAlarms()
                        }
                    }
                }
            }
            .setView(view)

        val dialog: Dialog = builder.create()

        lifecycleScope.launchWhenStarted {
            viewModel.utility.collect {
                when (it) {
                    is DialogViewModel.DialogUtils.DisplayError -> {
                        Toast.makeText(requireActivity(), it.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        when(viewModel.card) {
            0 -> {
                editText = view.findViewById(R.id.editText)
                editText.setText(viewModel.name)
                editText.requestFocus()
                dialog.window?.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
            5 -> {
                editText = view.findViewById(R.id.editText)
                editText.setText(viewModel.seat)
                editText.requestFocus()
                dialog.window?.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
            else -> {
                spinner = view.findViewById(R.id.editSpinner)
                val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, viewModel.items)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.setSelection(viewModel.selected)
            }
        }


        return dialog
    }


    private fun cancelAlarms() {
        AlarmUtils.cancelAllAlarms(requireContext(), Intent(requireActivity(), NotificationSender::class.java))
    }

}