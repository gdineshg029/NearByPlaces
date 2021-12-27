package com.java.nearbyplaces.ui.home.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationLiveData = LocationLiveData(application.applicationContext)
    fun getLocationLiveData() = locationLiveData
}