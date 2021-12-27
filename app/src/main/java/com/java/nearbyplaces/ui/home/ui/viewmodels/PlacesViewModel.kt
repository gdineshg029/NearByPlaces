package com.java.nearbyplaces.ui.home.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.java.nearbyplaces.data.model.LocationDetails
import com.java.nearbyplaces.data.model.PlaceDetails
import com.java.nearbyplaces.data.repository.AppRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PlacesViewModel(private val appRepository: AppRepository) : ViewModel() {

    val placesList = MutableLiveData<List<PlaceDetails>>()
    val loadingStatus = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    val successState = "OK"
    val failureState = "REQUEST_DENIED"

    val compositeDisposable = CompositeDisposable()


    fun getPlaces(query: String, currentLocationDetails: LocationDetails?) {

        loadingStatus.value = true

        val placesObservable = appRepository
            .getNearPlacesByLocation(query, currentLocationDetails)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        val placesDisposable = placesObservable.subscribe({ placesResponse ->
            loadingStatus.value = false
            if (placesResponse.statusMessage.equals(successState))
                placesList.value = placesResponse.placesList
            else
                errorMessage.value = placesResponse.errorMessage
        }, {
            loadingStatus.value = false
            errorMessage.value = it.message
        })

        compositeDisposable.add(placesDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}