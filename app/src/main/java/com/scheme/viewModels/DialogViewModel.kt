package com.scheme.viewModels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.scheme.App
import com.scheme.R
import com.scheme.data.EventRepository
import com.scheme.data.LectureRepository
import com.scheme.di.ApplicationScope
import com.scheme.models.DayEvent
import com.scheme.models.Lecture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(
    application: Application,
    private val lectureRepository: LectureRepository,
    private val eventRepository: EventRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
    val state: SavedStateHandle
) : AndroidViewModel(application) {

    val card = state.get<Int>("card") ?: -1
    val items = state.get<Array<String>>("items")?.toList() ?: listOf("")

    private val cardTitles = intArrayOf(R.string.tab3_name, R.string.tab3_university, R.string.tab3_faculty,
                                        R.string.tab3_year, R.string.tab3_section, R.string.tab3_seatnumber)
    private val dialogLayouts = intArrayOf(R.layout.edit_items, R.layout.edit_text)

    private val sharedPreferences: SharedPreferences = (application).getSharedPreferences(App.SHARED_PREFS, Context.MODE_PRIVATE)

    val name: String
        get() = sharedPreferences.getString(App.NAME, "") ?: ""
    private val university: String
        get() = sharedPreferences.getString(App.UNIVERSITY, "") ?: ""
    val faculty: String
        get() = sharedPreferences.getString(App.FACULTY, "") ?: ""
    val year: String
        get() = sharedPreferences.getString(App.SCHOOLYEAR, "") ?: ""
    val section: String
        get() = sharedPreferences.getString(App.SECTION, "") ?: ""
    val seat: String
        get() = sharedPreferences.getString(App.SEAT, "") ?: ""
    private var previousYear: String? = null



    fun getTitle(): Int {
        return cardTitles[card]
    }

    fun getLayout(): Int {
        return when(card) {
            0 -> dialogLayouts[1]
            5 -> dialogLayouts[1]
            else -> {
                dialogLayouts[0]
            }
        }
     }

    val selected: Int
    get() {
        return when (card) {
            1 -> items.indexOf(university)
            2 -> items.indexOf(faculty)
            3 -> items.indexOf(year)
            4 -> items.indexOf(section)
            else -> {
                0
            }
        }
    }


    fun onSave(newItem: String) {
        val editor = sharedPreferences.edit()
        when(card) {
            0 -> saveNew(newItem)
            1 -> restoreItems(newItem)
            2 -> restoreItems(newItem)
            3 -> restoreItems(newItem)
            4 -> saveNew(newItem)
            5 -> saveNew(newItem)
        }
        editor.apply()
    }

    private fun saveNew(item: String) {
        removeOld()
        val editor = sharedPreferences.edit()
        when(card) {
            0 -> editor.putString(App.NAME, item)
            1 -> editor.putString(App.UNIVERSITY, item)
            2 ->  editor.putString(App.FACULTY, item)
            3 -> {
                previousYear = year
                editor.putString(App.SCHOOLYEAR, item)
            }
            4 -> editor.putString(App.SECTION, item)
            5 -> editor.putString(App.SEAT, item)
        }
        editor.apply()
    }

    private fun removeOld() {
        val editor = sharedPreferences.edit()
        when(card) {
            1 -> {
                editor.remove(App.FACULTY)
                editor.remove(App.SCHOOLYEAR)
                editor.remove(App.SECTION)
                editor.remove(App.VERSION)
            }
            2 -> {
                editor.remove(App.SCHOOLYEAR)
                editor.remove(App.SECTION)
                editor.remove(App.VERSION)
            }
            3 -> {
                editor.remove(App.SECTION)
                editor.remove(App.VERSION)
            }
        }
        editor.apply()
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, _ ->
        showError()
        previousYear?.let { saveNew(it) }
   }

    private suspend fun reset() {
        lectureRepository.deleteAll()
        eventRepository.deleteAll()
    }

    private fun restoreItems(item: String) {
        saveNew(item)
        applicationScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            var lectures: List<Lecture>? = null
            if (card == 3 && faculty.isNotBlank() && university.isNotBlank()) {
                lectures = lectureRepository.requestData(
                    formatPath(university),
                    formatPath(faculty),
                    formatPath(item)
                )
                val version = lectureRepository.getVersion(
                    formatPath(university),
                    formatPath(faculty),
                    formatPath(year)
                )
                val editor = sharedPreferences.edit()
                editor.putString(App.VERSION, version)
                editor.apply()
            }

            reset()
            if (lectures != null) {
                for (lecture in lectures) {
                    lectureRepository.insert(lecture)
                    eventRepository.insert(
                        DayEvent(
                            lecture.lecture,
                            lecture.day_value,
                            lecture.startHour,
                            lecture.startMinute,
                            lecture.endHour,
                            lecture.endMinute,
                            randomColor(),
                            lecture.section
                        )
                    )
                }
            }
        }
    }


    private fun randomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(150), rnd.nextInt(150), rnd.nextInt(150))
    }

    private fun formatPath(text: String): String {
        return text.lowercase().filter { !it.isWhitespace() }
    }

    private val utilChannel = Channel<DialogUtils>()
    val utility = utilChannel.receiveAsFlow()


    private fun showError() =
        viewModelScope.launch {
            utilChannel.send(DialogUtils.DisplayError(getApplication<Application>().getString(R.string.ConnectionFailed)))
        }


    sealed class DialogUtils {
        data class DisplayError(val msg: String) : DialogUtils()
    }
}
