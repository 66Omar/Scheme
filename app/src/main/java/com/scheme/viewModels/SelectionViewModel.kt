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
    var items: LiveData<List<String>>? = null

    fun getList(): LiveData<List<String>>? {
        viewModelScope.launch {
        items = lectureRepository.getLectureTitles().asLiveData()
        }
        return items
    }
}