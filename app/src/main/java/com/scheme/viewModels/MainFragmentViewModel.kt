package com.scheme.viewModels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scheme.App
import com.scheme.R
import com.scheme.data.LectureRepository
import com.scheme.di.ApplicationScope
import com.scheme.utilities.SchemeUtils.formatPath
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject


@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    application: Application,
    private val lectureRepository: LectureRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : AndroidViewModel(application) {
    private val sharedPreferences: SharedPreferences = (application).getSharedPreferences(App.SHARED_PREFS, Context.MODE_PRIVATE)

    val name: String?
    get() = sharedPreferences.getString(App.NAME, null)
    val university: String?
    get() = sharedPreferences.getString(App.UNIVERSITY, null)
    val faculty: String?
    get() = sharedPreferences.getString(App.FACULTY, null)
    val year: String?
    get() = sharedPreferences.getString(App.SCHOOLYEAR, null)
    val section: String?
    get() = sharedPreferences.getString(App.SECTION, null)
    val seat: String?
    get() = sharedPreferences.getString(App.SEAT, null)

    val currentVersion = MutableLiveData<String>()
    val savedVersion: String?
    get() = sharedPreferences.getString(App.VERSION, null)

    fun shouldSetup(): Boolean {
        return (name == null && university == null && faculty == null && year == null
                && section == null && seat == null)
    }

    fun shouldCompleteSetup(): Boolean {
        return (university == null || faculty == null || year == null || section == null)
    }

    fun shouldUpdate(): MutableLiveData<String> {
        if (university != null && faculty != null && year != null) {
            viewModelScope.launch(Dispatchers.Default + coroutineExceptionHandler) {
                currentVersion.postValue(
                    lectureRepository.getVersion(
                        formatPath(university),
                        formatPath(faculty),
                        formatPath(year)
                    )
                )
            }
        }
        return currentVersion
    }

    fun update(newVersion: String) {
        if (university != null && faculty != null && year != null) {
            applicationScope.launch(Dispatchers.IO + coroutineExceptionHandlerUpdate) {
                lectureRepository.requestData(formatPath(university), formatPath(faculty), formatPath(year))
                val editor = sharedPreferences.edit()
                editor.putString(App.VERSION, newVersion)
                editor.apply()
            }
        }
    }



    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, _ ->
        //pass
    }
    private val coroutineExceptionHandlerUpdate = CoroutineExceptionHandler{ _, _ ->
        showError()
    }


    private val utilChannel = Channel<MainFragmentUtils>()
    val utility = utilChannel.receiveAsFlow()

    private fun showError() =
        viewModelScope.launch {
            utilChannel.send(MainFragmentUtils.DisplayError(getApplication<Application>().getString(R.string.ConnectionFailed)))
        }


    sealed class MainFragmentUtils {
        data class DisplayError(val msg: String) : MainFragmentUtils()
    }

}