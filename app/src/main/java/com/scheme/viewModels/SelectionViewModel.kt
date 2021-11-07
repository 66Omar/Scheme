package com.scheme.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.scheme.data.LectureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor(
    application: Application,
    private val lectureRepository: LectureRepository,
) : AndroidViewModel(application) {

    val lectureTitles: LiveData<List<String>> = lectureRepository.getLectureTitles().asLiveData()

}