package com.scheme.viewModels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.scheme.App
import com.scheme.data.EventRepository
import com.scheme.models.DayEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TwoViewModel @Inject constructor(
    application: Application,
    private val eventRepository: EventRepository,
    ):
    AndroidViewModel(application) {
    private val sharedPreferences: SharedPreferences =
        getApplication<Application>().getSharedPreferences(App.SHARED_PREFS, Context.MODE_PRIVATE)

    val savedSection
    get() = sharedPreferences.getString(App.SECTION, null)


    val storedEvents: LiveData<List<DayEvent>>?
        get() {
            if (savedSection != null) {
                return eventRepository.getAll(savedSection!!).asLiveData()
            }
            return null
        }

}