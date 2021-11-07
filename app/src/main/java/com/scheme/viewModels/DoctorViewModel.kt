package com.scheme.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.scheme.data.LectureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DoctorViewModel @Inject constructor(
    application: Application,
    private val lectureRepository: LectureRepository,
    state: SavedStateHandle
) : AndroidViewModel(application) {

    val lectureTitle = state.get<String>("lecture") ?: ""

    val doctorsList: LiveData<List<String>> = lectureRepository.getDoctorNames(lectureTitle).asLiveData()

}