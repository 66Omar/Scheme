package com.scheme

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.scheme.databinding.EditEventBinding
import com.scheme.models.DayEvent
import com.scheme.utilities.EventTime
import com.scheme.utilities.NotificationSchedule
import com.scheme.viewModels.EditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnFastChooseColorListener

@AndroidEntryPoint
class EditEvent : Fragment() {
    private val editViewModel: EditViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = EditEventBinding.inflate(inflater, container, false)

        val view = binding.root
        if (editViewModel.id == (-1).toLong()) {
            binding.editToolbar.title = "Add event"
        } else {
            binding.editToolbar.title = "Edit event"
            binding.btndelete.visibility = View.VISIBLE
            binding.btndelete.setOnClickListener {
                editViewModel.onDeleteClick()
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            editViewModel.utility.collect { event ->
                when (event) {
                    is EditViewModel.EditUtils.DisplayError -> {
                        Toast.makeText(requireActivity(), event.msg, Toast.LENGTH_LONG).show()
                    }
                    is EditViewModel.EditUtils.OperationSuccess -> {
                        updateAlarms(event.state, event.event)
                        findNavController().navigateUp()
                        }
                    }
                }
            }



        binding.editToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.Task.setText(editViewModel.taskName)
        binding.Color.setBackgroundColor(editViewModel.taskColor)
        binding.weekday.setSelectedDay(editViewModel.taskDay)
        binding.startTime.text =
            EventTime(
                editViewModel.startHour,
                editViewModel.startMin
            ).string
        binding.endTime.text = EventTime(
            editViewModel.endHour,
            editViewModel.endMin
        ).string
        binding.Task.addTextChangedListener {
            editViewModel.taskName = it.toString()
        }

        binding.weekday.setOnDaySelectedListener { day -> editViewModel.taskDay = day }
        binding.Color.setOnClickListener {
            val colorPicker = ColorPicker(requireActivity())
            colorPicker.setOnFastChooseColorListener(object : OnFastChooseColorListener {
                override fun setOnFastChooseColorListener(position: Int, color: Int) {
                    binding.Color.setBackgroundColor(color)
                    editViewModel.taskColor = color
                }

                override fun onCancel() {}
            })
                .setColumns(5)
                .setTitle("Choose a color")
                .show()
        }
        binding.startCard.setOnClickListener {
            val mTimePicker = TimePickerDialog(
                activity,
                R.style.DialogTheme,
                { _, selectedHour, selectedMinute ->
                    val eventStart =
                        EventTime(
                            selectedHour,
                            selectedMinute
                        )
                    binding.startTime.text = eventStart.string
                    editViewModel.startHour = selectedHour
                    editViewModel.startMin = selectedMinute
                },
                editViewModel.startHour,
                editViewModel.startMin,
                false
            )
            mTimePicker.show()
        }
        binding.endCard.setOnClickListener {
            val mTimePicker = TimePickerDialog(
                activity,
                R.style.DialogTheme,
                { _, selectedHour, selectedMinute ->
                    val eventEnd = EventTime(
                        selectedHour,
                        selectedMinute
                    )
                    binding.endTime.text = eventEnd.string
                    editViewModel.endHour = selectedHour
                    editViewModel.endMin = selectedMinute
                },
                editViewModel.endHour,
                editViewModel.endMin,
                false
            )
            mTimePicker.show()
        }

        binding.btnadd.setOnClickListener {
            editViewModel.onSaveClick()
        }
        return view
    }

    private fun updateAlarms(state: Int, event: DayEvent) {
        if (state == 1) {
            NotificationSchedule.cancelEventNotifications(requireContext(), event)
        }
        else {
            NotificationSchedule.scheduleEventNotification(requireContext(), event)
        }
    }
}