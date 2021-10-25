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
    var items: LiveData<List<String>>? = null

    val lectureTitle = state.get<String>("lecture") ?: ""

    fun getList(): LiveData<List<String>>? {
        viewModelScope.launch {
            items = lectureRepository.getDoctorNames(lectureTitle).asLiveData()
        }
        return items
    }
}