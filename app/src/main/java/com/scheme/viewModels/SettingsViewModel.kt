package com.scheme.viewModels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.scheme.App
import com.scheme.R
import com.scheme.data.LectureRepository
import com.scheme.utilities.SchemeUtils.formatPath
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val lectureRepository: LectureRepository) : AndroidViewModel(application) {


    private val sharedPreferences: SharedPreferences = (application).getSharedPreferences(App.SHARED_PREFS, Context.MODE_PRIVATE)

     val name: String
     get() {
         return sharedPreferences.getString(App.NAME, "") ?: ""
     }
    val university: String
        get() {
            return sharedPreferences.getString(App.UNIVERSITY, "") ?: ""
        }
    val faculty: String
        get() {
            return sharedPreferences.getString(App.FACULTY, "") ?: ""
        }
    val year: String
        get() {
            return sharedPreferences.getString(App.SCHOOLYEAR, "") ?: ""
        }
    val section: String
        get() {
            return sharedPreferences.getString(App.SECTION, "") ?: ""
        }
    val seat: String
        get() {
            return sharedPreferences.getString(App.SEAT, "") ?: ""
        }

    private var years = MutableLiveData<List<String>>()
    private var universities = MutableLiveData<List<String>>()
    private var faculties = MutableLiveData<List<String>>()
    private var sections: LiveData<List<String>>? = null

    fun shouldSetup(): Boolean {
        return (name.isBlank() && university.isBlank() && faculty.isBlank() && year.isBlank()
                && section.isBlank() && seat.isBlank())
    }

    fun availableUni(): MutableLiveData<List<String>> {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            universities.postValue(lectureRepository.getUniversities())
        }
        return universities
    }

    fun availableFaculty(): MutableLiveData<List<String>> {
        if (university.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                faculties.postValue(lectureRepository.getFaculties(formatPath(university)))
            }
        }
        return faculties
    }

    fun availableYears(): MutableLiveData<List<String>> {
        if (university.isNotBlank() && faculty.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                years.postValue(lectureRepository.getYears(formatPath(university), formatPath(faculty)))
            }
        }
        return years
    }

    fun availableSections(): LiveData<List<String>>? {
        if (university.isNotBlank() && faculty.isNotBlank() && year.isNotBlank()) {
            sections = lectureRepository.getAllSections().asLiveData()
        }
        return sections
    }

    fun validateFaculty(): Boolean {
        return university.isNotBlank()
    }

    fun validateYear(): Boolean {
        return university.isNotBlank() && faculty.isNotBlank()
    }

    fun validateSection(): Boolean {
        return university.isNotBlank() && faculty.isNotBlank() && year.isNotBlank()
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable  ->
        showError()
        throwable.printStackTrace()

    }


    private val utilChannel = Channel<SettingsUtils>()
    val utility = utilChannel.receiveAsFlow()


    private fun showError() =
        viewModelScope.launch {
            utilChannel.send(SettingsUtils.DisplayError(getApplication<Application>().getString(R.string.ConnectionRetry)))
        }



    sealed class SettingsUtils {
        data class DisplayError(val msg: String) : SettingsUtils()
    }

}