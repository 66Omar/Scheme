package com.scheme.viewModels

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.scheme.models.DayEvent
import com.scheme.data.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    application: Application,
    private val eventRepository: EventRepository,
    private val state: SavedStateHandle,
    ) : AndroidViewModel(application) {

    val event = state.get<DayEvent>("event")

    val id: Int = event?.id ?: -1
    var taskName = state.get<String>("taskName") ?: event?.task ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskDay = state.get<Int>("taskDay") ?: event?.day ?: 0
        set(value) {
            field = value
            state.set("taskDay", value)
        }

    var taskColor = state.get<Int>("taskColor") ?: event?.color ?: Color.parseColor("#ff9500")
        set(value) {
            field = value
            state.set("taskColor", value)
        }

    var startHour = state.get<Int>("taskStartHour") ?: event?.start_hour ?: 12
        set(value) {
            field = value
            state.set("taskStartHour", value)
        }

    var startMin = state.get<Int>("taskStartMin") ?: event?.start_min ?: 0
        set(value) {
            field = value
            state.set("taskStartMin", value)
        }

    var endHour = state.get<Int>("taskEndHour") ?: event?.end_hour ?: 13
        set(value) {
            field = value
            state.set("taskEndHour", value)
        }

    var endMin = state.get<Int>("taskEndMin") ?: event?.end_min ?: 0
        set(value) {
            field = value
            state.set("taskEndMin", value)
        }

    var taskAuto = state.get<String>("taskAuto") ?: event?.auto ?: "user"
        set(value) {
            field = value
            state.set("taskAuto", value)
        }

    private val utilChannel = Channel<EditUtils>()
    val utility = utilChannel.receiveAsFlow()

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        utilChannel.send(EditUtils.DisplayError(text))
    }

    private fun operationStatus(state: Int) =
        viewModelScope.launch {
            utilChannel.send(EditUtils.OperationSuccess(state))
        }



    private fun verify(): Boolean {
        if (startHour < endHour) {
            return true
        }
        return if (startHour == endHour) {
            startMin < endMin
        } else false
    }

    private fun update(event: DayEvent) {
        viewModelScope.launch {
        eventRepository.update(event)
        }
        }

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidInputMessage("Task can't be empty")
            return
        }
        if (!verify()) {
            showInvalidInputMessage("Events should be within the same day")
            return
        }


        if (taskName.isNotBlank() && verify()) {
            val updatedEvent = DayEvent(taskName, taskDay, startHour, startMin, endHour, endMin, taskColor, taskAuto)
            if (id != -1) {
                updatedEvent.id = id
                update(updatedEvent)
                operationStatus(0)
            }
            else {
                viewModelScope.launch { eventRepository.insert(updatedEvent)
                    operationStatus(0)
                }
            }
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            if (event != null) {
                eventRepository.delete(event)
                operationStatus(1)
            }
        }
    }

    sealed class EditUtils {
        data class DisplayError(val msg: String) : EditUtils()
        data class OperationSuccess(val state: Int) : EditUtils()

    }

}