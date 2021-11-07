package com.scheme

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager.LayoutParams
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.scheme.utilities.NotificationSchedule
import com.scheme.viewModels.DialogViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class Dialog : DialogFragment() {
    private lateinit var spinner: Spinner
    private lateinit var editText: EditText
    private lateinit var loadingDialog: LoadingDialog

    private val viewModel: DialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(viewModel.getLayout(), null)



        val builder = AlertDialog.Builder(requireContext())
            .setTitle(viewModel.getTitle())
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)


            .setView(view)

        val dialog: Dialog = builder.create()
        dialog.setOnShowListener {

        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            when (viewModel.card) {
                    0 -> viewModel.onSave(editText.text.toString())
                    5 -> viewModel.onSave(editText.text.toString())
                    else -> {
                        if (spinner.selectedItem != null) {
                            viewModel.onSave(spinner.selectedItem.toString())
                        }
                    }

                }
        }
        }

        lifecycleScope.launch {
            viewModel.utility.collect {
                when (it) {
                    is DialogViewModel.DialogUtils.OperationLoading -> {
                        showLoading()
                    }
                    is DialogViewModel.DialogUtils.DisplayError -> {
                        Toast.makeText(requireActivity(), it.msg, Toast.LENGTH_SHORT).show()
                        dismissLoading()
                        dismiss()
                    }
                    is DialogViewModel.DialogUtils.OperationSuccess -> {
                        if (it.cancel) {
                            val canceling = async(Dispatchers.IO) {
                                cancelAlarms()
                            }
                            canceling.await()
                            dismissLoading()
                            dismiss()
                        }
                        else {
                            dismiss()
                        }

                    }
                    is DialogViewModel.DialogUtils.SendItems -> {
                        val scheduling = async(Dispatchers.IO) {
                        cancelAlarms()
                        NotificationSchedule.scheduleLecturesNotifications(requireContext(), it.items)
                        }
                        scheduling.await()
                        dismissLoading()
                        dismiss()
                    }
                }
            }
        }


        when(viewModel.card) {
            0 -> {
                editText = view.findViewById(R.id.editText)
                editText.setText(viewModel.name)
                editText.requestFocus()
                dialog.window?.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            }
            5 -> {
                editText = view.findViewById(R.id.editText)
                editText.setText(viewModel.seat)
                editText.requestFocus()
                dialog.window?.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE)
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
        NotificationSchedule.cancelAllLectureNotifications(requireContext())
    }

    private fun showLoading() {
        loadingDialog = LoadingDialog()
        loadingDialog.show(childFragmentManager, "dialog")
    }

    private fun dismissLoading() {
        if (::loadingDialog.isInitialized) {
            loadingDialog.dismiss()
        }
    }

}