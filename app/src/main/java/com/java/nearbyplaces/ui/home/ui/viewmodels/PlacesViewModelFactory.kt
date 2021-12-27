package com.java.nearbyplaces.ui.home.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.java.nearbyplaces.data.repository.AppRepository

class PlacesViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PlacesViewModel::class.java)){
            return PlacesViewModel(appRepository) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}