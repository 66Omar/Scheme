package com.scheme.viewModels

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.*
import com.scheme.data.EventRepository
import com.scheme.data.LectureRepository
import com.scheme.di.ApplicationScope
import com.scheme.models.DayEvent
import com.scheme.models.Lecture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class LectureSelectionViewModel @Inject constructor(
    application: Application,
    private val lectureRepository: LectureRepository,
    private val eventRepository: EventRepository,
    state: SavedStateHandle,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : AndroidViewModel(application) {
    var items: LiveData<List<Lecture>>? = null

    private val lectureTitle = state.get<String>("lecture") ?: ""
    private val doctorName = state.get<String>("doctor") ?: ""

    fun getList(): LiveData<List<Lecture>>? {
        viewModelScope.launch {
            items = lectureRepository.getFilteredLectures(lectureTitle, doctorName).asLiveData()
                .map { list -> list.sortedBy { item -> item.timeLeft } }
        }
        return items
    }

    fun insert(lecture: Lecture) {
        applicationScope.launch {
            lecture.auto = 0
            lectureRepository.insert(lecture)
            val rnd = Random()
            val color: Int = Color.argb(255, rnd.nextInt(150), rnd.nextInt(150), rnd.nextInt(150))
            eventRepository.insert(DayEvent(lecture.lecture, lecture.day_value, lecture.startHour, lecture.startMinute, lecture.endHour, lecture.endMinute, color, "user"))
        }
    }
}