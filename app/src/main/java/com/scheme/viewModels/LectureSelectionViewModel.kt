package com.scheme.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.scheme.data.EventRepository
import com.scheme.data.LectureRepository
import com.scheme.di.ApplicationScope
import com.scheme.models.DayEvent
import com.scheme.models.Lecture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.scheme.utilities.SchemeUtils.generateColor


@HiltViewModel
class LectureSelectionViewModel @Inject constructor(
    application: Application,
    private val lectureRepository: LectureRepository,
    private val eventRepository: EventRepository,
    state: SavedStateHandle,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : AndroidViewModel(application) {

    private val lectureTitle = state.get<String>("lecture") ?: ""
    private val doctorName = state.get<String>("doctor") ?: ""

    val lecturesList: LiveData<List<Lecture>> = lectureRepository.getFilteredLectures(lectureTitle, doctorName).asLiveData()
                .map { list -> list.sortedBy { item -> item.timeLeft } }


    fun update(lecture: Lecture) {
        applicationScope.launch {
            lecture.auto = 0
            lectureRepository.update(lecture)
            eventRepository.insert(DayEvent(lecture.lecture, lecture.day_value, lecture.startHour, lecture.startMinute, lecture.endHour, lecture.endMinute, generateColor(), "user"))
        }
    }
}