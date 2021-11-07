package com.scheme.viewModels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.scheme.*
import com.scheme.data.LectureRepository
import com.scheme.models.Lecture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class OneViewModel @Inject constructor(
    application: Application,
    private val repository: LectureRepository
) : AndroidViewModel(application) {
    private val sharedPreferences: SharedPreferences = getApplication<Application>().getSharedPreferences(
        App.SHARED_PREFS, Context.MODE_PRIVATE)
    val savedSection
    get() = sharedPreferences.getString(App.SECTION, null)

    val stored: LiveData<List<Lecture>>?
        get() {
            if (!savedSection.isNullOrBlank()) {
                    return repository.getAll(savedSection!!).asLiveData().map { list -> list.sortedBy { item -> item.timeLeft } }
                }
            return null
        }

    fun delete(lecture: Lecture) {
        viewModelScope.launch {
            repository.delete(lecture)
        }
    }


}